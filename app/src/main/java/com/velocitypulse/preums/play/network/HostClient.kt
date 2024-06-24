package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.play.HostClientInfo
import com.velocitypulse.preums.play.HostInfo
import com.velocitypulse.preums.play.HostInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class HostClient : Host() {

    private var discoveringJob: Job? = null
    private var connectionJob: Job? = null

    private val hostClientInfo = HostClientInfo(ApplicationInitializer.deviceName)

//    val discoveredParties = flowOf<HostInstance>()

    suspend fun startDiscovering(context: Context) {
        receiveBroadcast().collect {
            Log.d("debugPreums", "finally got $it")
            contactServer2(it)
        }
    }

    private fun receiveBroadcast() = callbackFlow {
        Log.d("debugPreums", "receiveBroadcast")

        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket(DISCOVERING_PORT, InetAddress.getByName("0.0.0.0"))

            while (isActive) {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)

                val message =
                    String(packet.data, 0, packet.length)
                Log.d("debugPreums", "Broadcast received: $message")

                try {
                    val hostInstance = Json.decodeFromString<HostInstance>(message)
                    Log.d("debugPreums", "HostInstance received: $hostInstance")
                    trySend(hostInstance)
                } catch (e: Exception) {
                    Log.w(
                        "debugPreums",
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

    private suspend fun contactServer2(hostInstance: HostInstance) {
        withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                socket = Socket(hostInstance.ip, hostInstance.port)

                val receiveChannel = socket.openReadChannel()
                Log.d("debug", "receive channel opened")

                val sendChannel = socket.openWriteChannel()
                Log.d("debug", "send channel opened")

                val infoMessage = Json.encodeToString(hostClientInfo)
                sendChannel.writeLine(infoMessage)
                Log.d(
                    "debugPreums",
                    "Sent 'hello' to server at ${hostInstance.ip}:${hostInstance.port}"
                )

                val response = receiveChannel.readLine()
                Log.d("debugPreums", "Received response from server: $response")
            } catch (e: Exception) {
                Log.e("debugPreums", "Failed to contact server: ${e.message}")
                e.printStackTrace()
            } finally {
                socket?.close()
            }
        }
    }

    private suspend fun contactServer(hostInstance: HostInstance) =
        coroutineScope {
            launch(Dispatchers.IO) {

                val socketFactory = getSecuredSocketFactory(context).socketFactory
                socketFactory.createSocket()/*.use */.let { socket -> // TODO : put use

                    Log.d("debug", "Starting contact socket")
                    // TODO : handle ConnectException
                    socket.connect(InetSocketAddress(hostInstance.ip, hostInstance.port))
                    Log.d("debug", "Contact socket connected")

                    val receiveChannel = socket.openReadChannel()
                    Log.d("debug", "receive channel opened")

                    val sendChannel = socket.openWriteChannel()
                    Log.d("debug", "send channel opened")

                    Log.d("debug", "reading one debug line : " + receiveChannel.readLine())

//                    sendChannel.writeLine("test")

//                    while(true) delay(500)

                    val hostInfo = HostInfo(
                        name = receiveChannel.readLine()!!,
                        playersCount = receiveChannel.readLine()!!.toInt(),
                        isLocked = receiveChannel.readLine()!!.toBoolean(),
                        password = null,
                        primaryColor = receiveChannel.readLine()!!.toInt()
                    )

//                    Log.d("debug", "Contact established: $hostInfo")
                }
            }
        }

    override fun stopProcedures() {
    }
}