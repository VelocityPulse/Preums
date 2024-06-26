package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.velocitypulse.preums.play.ClientInfo
import com.velocitypulse.preums.play.InstanceInfo
import com.velocitypulse.preums.play.ServerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.ServerSocket
import java.net.Socket
import javax.net.ssl.SSLServerSocket
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class HostServer : Host() {

    companion object {
        private val CONNECTION_PORT = Random.nextInt(from = 49151, until = 65534)
    }

//    private val serverSocket = ServerSocket()

//    private val receivedSockets = mutableListOf<Socket>()

//    val discoveredHost = flowOf<HostInstance>()


    private val _discoveredHostSharedFlow = MutableStateFlow<Set<ClientInfo>>(mutableSetOf())
    val discoveredHostSharedFlow: StateFlow<Set<ClientInfo>>
        get() = _discoveredHostSharedFlow

    private var broadcastJob: Job? = null
    private var serverInfoJob: Job? = null

    val hostInstances: Flow<ServerInfo> = flow {
        while (true) {
            delay(500)
        }
    }

    suspend fun startServer(context: Context) {
        broadcastJob?.cancel()
        broadcastJob = CoroutineScope(coroutineContext).launch {
            startSendBroadcast(context)
        }

        serverInfoJob?.cancel()
        serverInfoJob = CoroutineScope(coroutineContext).launch {
            startServerInfo()
        }
    }

    private suspend fun startSendBroadcast(context: Context) = withContext(Dispatchers.IO) {
        val address = getBroadcast(getLocalIP(context)!!)!!
        Log.d("debugPreums", "Broadcast sent to : $address")

        val serverInfo = ServerInfo(
            getLocalIP(context)!!.hostAddress!!,
            CONNECTION_PORT,
            null
        )

        val message = Json.encodeToString(serverInfo)

        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket()
            socket.broadcast = true

            val buffer = message.toByteArray()
            val packet =
                DatagramPacket(buffer, buffer.size, address, DISCOVERING_PORT)

            while (isActive) {
                socket.send(packet)
                Log.d("UDP", "Broadcast sent $address: $message")
                delay(1000)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("UDP", "Broadcast failed", e)
        } finally {
            if (socket != null && !socket.isClosed) {
                socket.close()
            }
        }
    }

    private suspend fun startServerInfo() = withContext(Dispatchers.IO) {
        Log.d("debugPreums", "Server starting on port $CONNECTION_PORT")

        val serverSocket = ServerSocket(CONNECTION_PORT)

        try {
            while (isActive) {
                Log.d("debugPreums", "Waiting for incoming connections...")
                val clientSocket = serverSocket.accept()

                launch {
                    Log.d(
                        "debugPreums",
                        "Client connected: ${clientSocket.inetAddress.hostAddress}"
                    )
                    handleClient(clientSocket)
                }
            }
        } catch (e: Exception) {
            Log.e("debugPreums", "Server error: ${e.message}")
            e.printStackTrace()
        } finally {
            serverSocket.close()
            Log.d("debugPreums", "Server socket closed")
        }
    }

    private suspend fun handleClient(clientSocket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val receiveChannel = clientSocket.openReadChannel()
                val sendChannel = clientSocket.openWriteChannel()
                Log.d("debugPreums", "channels opened")

                val message = receiveChannel.readLine() // TODO never receiving the line
                Log.d("debugPreums","Received message: $message")

                try {
                    val clientInfo = Json.decodeFromString<ClientInfo>(message)

                    _discoveredHostSharedFlow.value += clientInfo

                    val infoInstance = InstanceInfo(
                        name = "partie1",
                        playersCount = 5,
                        isLocked = false,
                        primaryColor = Color.Blue.toArgb(),
                        password = null
                    )

                    sendChannel.writeLine(Json.encodeToString(infoInstance))
                } catch (e: Exception) {
                    throw IllegalArgumentException("Received message : $message")
                }

            } catch (e: Exception) {
                Log.d("debugPreums","Error handling client: ${e.message}")
                e.printStackTrace()
            } finally {
                clientSocket.close()
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


            val infoInstance = InstanceInfo(
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
        broadcastJob?.cancel()
    }
}