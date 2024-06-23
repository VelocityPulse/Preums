package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import com.velocitypulse.preums.play.HostInfo
import com.velocitypulse.preums.play.HostInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class HostClient : Host() {

    private var discoveringJob: Job? = null
    private var connectionJob: Job? = null

    val discoveredParties = flowOf<HostInstance>()

    fun startDiscovering(context: Context) {

        Thread { receiveBroadcast() }.start()
    }

    private fun receiveBroadcast(): Flow<HostInstance> = callbackFlow {
        Log.d("debugPreums", "receiveBroadcast")

        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket(DISCOVERING_PORT, InetAddress.getByName("0.0.0.0"))

            while (true) {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)

                val message =
                    String(packet.data, 0, packet.length)
                Log.d("debugPreums", "Broadcast received: $message")

                message
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (socket != null && !socket.isClosed) {
                socket.close()
            }
        }
    }

    private suspend fun contactServer(context: Context, hostInstance: HostInstance) =
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