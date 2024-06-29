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
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

private val CONNECTION_PORT = Random.nextInt(from = 49151, until = 65534)
private const val TAG = "HostServer"

class HostServer : Host() {

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
        Log.i(TAG, "Broadcast sent to : $address")

        val serverInfo = ServerInfo(
            getLocalIP(context)!!.hostAddress!!,
            CONNECTION_PORT
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
                Log.i(TAG, "Broadcast sent $address: $message")
                delay(1000)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(TAG, "Broadcast failed", e)
        } finally {
            if (socket != null && !socket.isClosed) {
                socket.close()
            }
        }
    }

    private suspend fun startServerInfo() = withContext(Dispatchers.IO) {
        Log.i(TAG, "Server starting on port $CONNECTION_PORT")

        val serverSocket = ServerSocket(CONNECTION_PORT)
        try {
            while (isActive) {
                Log.i(TAG, "Waiting for incoming connections...")
                val clientSocket = serverSocket.accept()

                launch {
                    Log.i(TAG, "Client connected: ${clientSocket.inetAddress.hostAddress}")
                    handleClient(clientSocket)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Server error: ${e.message}")
            e.printStackTrace()
        } finally {
            serverSocket.close()
            Log.i(TAG, "Server socket closed")
        }
    }

    private suspend fun handleClient(clientSocket: Socket) {
        Log.i(TAG, "Handling client")
        withContext(Dispatchers.IO) {
            val comHelper = ComHelper(clientSocket)

            Log.i(TAG, "Waiting for client info...")
            val message = comHelper.readLineACK()
            Log.i(TAG, "Client info received: $message")

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

                comHelper.writeLineACK(Json.encodeToString(infoInstance))
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error decoding client info: ${e.message} ${e.stackTraceToString()}"
                )
            } finally {
                clientSocket.close()
            }
        }
    }

    override fun stopProcedures() {
        Log.i(TAG, "Stopping procedures")
        broadcastJob?.cancel()
        serverInfoJob?.cancel()
    }
}