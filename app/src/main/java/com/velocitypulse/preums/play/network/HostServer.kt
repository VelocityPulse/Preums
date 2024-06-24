package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.velocitypulse.preums.play.HostInfo
import com.velocitypulse.preums.play.HostInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import javax.net.ssl.SSLServerSocket
import kotlin.random.Random

class HostServer : Host() {

    companion object {
        private val CONNECTION_PORT = Random.nextInt(from = 49151, until = 65534)
    }

    private val serverSocket = ServerSocket()

    private val receivedSockets = mutableListOf<Socket>()


    val hostInstances: Flow<HostInstance> = flow {
        while (true) {
            delay(500)
        }
    }

    fun startServer(context: Context) {
        val address1 = getBroadcast(getLocalIP(context)!!)!!
        Log.d("debugPreums", "Broadcast sent to : ${address1.hostAddress}")

        val hostInstance = HostInstance(
            getLocalIP(context)!!.hostAddress!!,
            CONNECTION_PORT,
            null
        )

        val message = Json.encodeToString(hostInstance)

        sendBroadcast(message, address1)
    }

    private fun sendBroadcast(
        message: String,
        address: InetAddress,
    ) {
        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket()
            socket.broadcast = true

            val buffer = message.toByteArray()
            val packet =
                DatagramPacket(buffer, buffer.size, address, 8888) // Port 8888

            socket.send(packet)
            Log.d("UDP", "Broadcast sent: $message")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("UDP", "Broadcast failed", e)
        } finally {
            if (socket != null && !socket.isClosed) {
                socket.close()
            }
        }
    }

    private suspend fun startGameServer(context: Context) {

        withContext(Dispatchers.IO) {
            Log.d("debug", "Starting game server")
            val serverSocketFactory = getSecuredSocketFactory(context).serverSocketFactory
            val serverSocket =
                serverSocketFactory.createServerSocket(Companion.CONNECTION_PORT) as SSLServerSocket
            serverSocket.enabledCipherSuites = serverSocket.supportedCipherSuites
            serverSocket.needClientAuth = true
            serverSocket.enabledProtocols = listOf("TLSv1.2").toTypedArray()
            Log.d("debug", "trying to accept contact")
            val clientSocket = serverSocket.accept()
            Log.d("debug", "Contact accepted")

            // TODO :
            // quand le serveur et le client ouvrent leurs channel, ca crash
            // verifier si les certificats sont bien ajoutés
            // par exemple dans le turst store ou autre

            val receiveChannel = clientSocket.openReadChannel()
            /*
                        val receiveChannel = clientSocket.getInputStream().let {
                            Log.d("debug", "a")
                            BufferedInputStream(it, 1024).let {
                                Log.d("debug", "b")
                                InputStreamReader(it).let {
                                    Log.d("debug", "c")
                                    BufferedReader(it)
                                }
                            }
                        }
                        Log.d("debug", "received " + receiveChannel.readLine())
            */

            Log.d("debug", "receive channel opened")


            /*            val sendChannel = BufferedWriter(
                            OutputStreamWriter(
                                BufferedOutputStream(
                                    clientSocket.getOutputStream(), 1024
                                )
                            )
                        )*/


            val sendChannel = clientSocket.openWriteChannel()
            Log.d("debug", "send channel opened")


            val infoInstance = HostInfo(
                name = "partie1",
                playersCount = 5,
                isLocked = false,
                primaryColor = Color.Blue.toArgb(),
                password = null
            )

            sendChannel.writeLine(infoInstance.name)
            sendChannel.writeLine(infoInstance.playersCount.toString())
            sendChannel.writeLine(infoInstance.isLocked.toString())
            sendChannel.writeLine(infoInstance.primaryColor.toString())
            Log.d("debug", "Info sent")
        }
    }

    override fun stopProcedures() {
    }
}