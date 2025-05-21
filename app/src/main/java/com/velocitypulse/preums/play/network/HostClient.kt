package com.velocitypulse.preums.play.network

import android.util.Log
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.play.ClientInfo
import com.velocitypulse.preums.play.ServerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private var discoveringJob: Job? = null
    private var connectionJob: Job? = null

    private val clientInfo = ClientInfo(ApplicationInitializer.deviceName)

    private val discoveredServers = mutableSetOf<ServerInfo>()

    suspend fun startDiscovering() {
        receiveBroadcast().collect {
            requestInformation(it)
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
                        Log.w(TAG, "Duplicated host instance: $serverInfo")
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

    private suspend fun requestInformation(serverInfo: ServerInfo) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, "Connecting to server at ${serverInfo.ip}:${serverInfo.port}")

            var socket: Socket? = null
            try {
                socket = Socket(serverInfo.ip, serverInfo.port)
                Log.i(TAG, "socket opened")

                val comHelper = ComHelper(socket)
                val infoMessage = Json.encodeToString(clientInfo)

                Log.i(
                    TAG,
                    "Sending [$infoMessage] to server at ${serverInfo.ip}:${serverInfo.port}"
                )
                comHelper.writeLineACK(infoMessage)
                val response = comHelper.readLineACK()

                Log.i(TAG, "Received response from server: $response")
                // TODO : Continue here
            } catch (e: Exception) {
                Log.e(TAG, "Failed to contact server: ${e.message}")
                e.printStackTrace()
            } finally {
                socket?.close()
            }
        }
    }

    override fun stopProcedures() {
        // TODO
    }
}
