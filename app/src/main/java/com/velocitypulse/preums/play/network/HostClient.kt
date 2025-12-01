package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.play.ClientInfo
import com.velocitypulse.preums.play.InstanceInfo
import com.velocitypulse.preums.play.ServerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

private const val TAG = "HostClient"

class HostClient(networkInfos: NetworkInfos) : Host(networkInfos) {

    private val discoveredServers = mutableSetOf<ServerInfo>()

    private val _discoveredServers = MutableStateFlow<Set<InstanceInfo>>(emptySet())
    val discoveredItems = _discoveredServers.asStateFlow()

    suspend fun startDiscovering(context: Context) {
        receiveBroadcast().collect {
            requestInformation(it, context)
        }
    }

    private fun receiveBroadcast() = callbackFlow {
        Log.i(TAG, "Starting broadcast")

        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket(DISCOVERING_PORT, InetAddress.getByName("0.0.0.0"))

            while (isActive) {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)

                val message =
                    String(packet.data, 0, packet.length)
                Log.i(TAG, "Broadcast received: $message")

                try {
                    val serverInfo = Json.decodeFromString<ServerInfo>(message)
                    Log.i(TAG, "HostInstance received: $serverInfo")
                    if (discoveredServers.add(serverInfo)) {
                        trySend(serverInfo)
                    } else {
                        Log.e(TAG, "Duplicated host instance: $serverInfo")
                    }
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
    }.flowOn(Dispatchers.IO)

    private suspend fun requestInformation(serverInfo: ServerInfo, context: Context) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, "Connecting to server at ${serverInfo.ip}:${serverInfo.port}")

            var socket: Socket? = null
            try {
                socket = Socket(serverInfo.ip, serverInfo.port)
                Log.i(TAG, "socket opened")

                val netHelper = NetHelper(socket)
                val infoMessage = Json.encodeToString(
                    ClientInfo(
                        ApplicationInitializer.deviceName,
                        getLocalIP(context)!!.hostAddress!!
                    )
                )

                Log.i(
                    TAG,
                    "Sending [$infoMessage] to server at ${serverInfo.ip}:${serverInfo.port}"
                )
                netHelper.writeLineACK(infoMessage)
                val response = netHelper.readLineACK()

                val instance: InstanceInfo = Json.decodeFromString<InstanceInfo>(response)
                Log.i(TAG, "ServerInstance received: $instance")
                _discoveredServers.value += instance

                // wait
                // TODO keepConnectedClient
                // TODO check that everybody correctly updates their values
//                while (true) {
//                    Thread.sleep(1000)
//                }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to contact server: ${e.message}")
                e.printStackTrace()
            } finally {
                Log.i(TAG, "socket closed")
                socket?.close()
            }
        }
    }

    // TODO : Now that we have many instance info, maybe we should regularely ping them to
    // be sure to keep them alive

    override fun stopProcedures() {
        // TODO
    }
}
