package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.play.ClientInfo
import com.velocitypulse.preums.play.InstanceInfo
import com.velocitypulse.preums.play.ServerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import kotlin.printStackTrace

private const val TAG = "HostClient"

class HostClient(private val context: Context) : Host() {

    private var clientJob: Job? = null

    private val _servers = MutableStateFlow<Set<InstanceInfo>>(emptySet())
    val discoveredItems = _servers.asStateFlow()

    suspend fun startClient() {
        clientJob = CoroutineScope(currentCoroutineContext()).launch {
            receiveBroadcast()
        }
    }

    private suspend fun receiveBroadcast() = withContext(Dispatchers.IO) {
        Log.i(TAG, "Starting broadcast")

        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket(DISCOVERING_PORT, InetAddress.getByName("0.0.0.0"))

            while (isActive) {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)

                val message = String(packet.data, 0, packet.length)

                try {
                    val serverInfo = Json.decodeFromString<ServerInfo>(message)
                    if (_servers.value.any { it.serverInfo == serverInfo }) {
                        continue
                    }

                    Log.i(TAG, "Broadcast HostInstance received: $serverInfo")
                    createSocket(serverInfo)
                } catch (e: Exception) {
                    Log.w(
                        TAG,
                        "Failed to decode host instance " + Exception(e).stackTraceToString()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (socket != null && !socket.isClosed) {
                socket.close()
            }
        }
    }

    private suspend fun createSocket(serverInfo: ServerInfo) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, "Connecting to server at ${serverInfo.ip}:${serverInfo.port}")

            var socket: Socket? = null
            try {
                socket = Socket(serverInfo.ip, serverInfo.port)
                Log.i(TAG, "socket opened")

                val netHelper = NetHelper(socket)
                sendClientInfo(netHelper)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to contact server: ${e.message}")
                e.printStackTrace()
            } finally {
                Log.i(TAG, "socket closed")
                socket?.close()
            }
        }
    }

    private suspend fun sendClientInfo(netHelper: NetHelper) {
        withContext(Dispatchers.IO) {
            try {
                val infoMessage = Json.encodeToString(
                    ClientInfo(
                        ApplicationInitializer.deviceName,
                        getLocalIP(context)!!.hostAddress!!
                    )
                )

                netHelper.writeLineACK(infoMessage)
                val response = netHelper.readLineACK()

                val instance: InstanceInfo = Json.decodeFromString<InstanceInfo>(response)
                Log.i(TAG, "ServerInstance received: $instance")
                _servers.update { it + instance }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to contact server: ${e.message}")
                e.printStackTrace()
            } finally {
                Log.i(TAG, "socket closed")
                netHelper.socket.close()
            }
        }
    }

    private suspend fun keepConnectedServer(serverInfo: ServerInfo, netHelper: NetHelper) =
        withContext(Dispatchers.IO) {
            try {
                while (isActive) {
                    delay(3000)
                    // Ping calculable ici
                    withTimeout(1000) {
                        netHelper.writeLineACK("Checking connection")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in keepConnectedClient: ${e.message}")
//                    _servers.update { it - serverInfo }
            }
        }

// TODO : Now that we have many instance info, maybe we should regularely ping them to
// be sure to keep them alive

    override fun stopProcedures() {
        // TODO
    }
}
