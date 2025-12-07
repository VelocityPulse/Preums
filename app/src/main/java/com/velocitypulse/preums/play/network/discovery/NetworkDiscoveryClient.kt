package com.velocitypulse.preums.play.network.discovery

import android.content.Context
import android.util.Log
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.play.network.ClientInfo
import com.velocitypulse.preums.play.network.ServerInfo
import com.velocitypulse.preums.play.network.ServerAddress
import com.velocitypulse.preums.play.network.core.NetHelper
import com.velocitypulse.preums.play.network.game.GameClient
import com.velocitypulse.preums.play.network.game.TAG
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

private const val TAG = "NetworkDiscoveryClient"

class NetworkDiscoveryClient(private val context: Context) : NetworkBase() {

    private var clientJob: Job? = null

    private val _discoveredServerInfos = MutableStateFlow<Set<ServerInfo>>(emptySet())
    val discoveredServerInfos = _discoveredServerInfos.asStateFlow()

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
                    val serverAddress = Json.decodeFromString<ServerAddress>(message)
                    if (_discoveredServerInfos.value.any { it.serverAddress == serverAddress }) {
                        continue
                    }

                    Log.i(TAG, "Broadcast HostInstance received: $serverAddress")
                    createSocket(serverAddress)
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

    private suspend fun createSocket(serverAddress: ServerAddress) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, "Connecting to server at ${serverAddress.ip}:${serverAddress.port}")

            var netHelper: NetHelper? = null
            try {
                // Socket creation
                netHelper = NetHelper(Socket(serverAddress.ip, serverAddress.port))
                Log.i(TAG, "socket opened")

                val clientInfo = ClientInfo(
                    ApplicationInitializer.deviceName,
                    getLocalIP(context)!!.hostAddress!!
                )
                val message = Json.encodeToString(clientInfo)
                netHelper.writeLineACK(message)


                val serverInfoMessage = netHelper.readLineACK()

                val serverInfo: ServerInfo = Json.decodeFromString<ServerInfo>(serverInfoMessage)
                Log.i(TAG, "ServerInstance received: $serverInfo")
                _servers.update { it + serverInfo }
                // todo lancer un GameClient depuis le VM

            } catch (e: Exception) {
                Log.e(TAG, "Failed to contact server: ${e.message}")
                e.printStackTrace()
            } finally {
                Log.i(TAG, "socket closed")
                netHelper?.socket?.close()
            }
        }
    }



    private suspend fun keepConnectedServer(serverAddress: ServerAddress, netHelper: NetHelper) =
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
