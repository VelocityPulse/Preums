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
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
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

class HostServer(networkInfos: NetworkInfos) : Host(networkInfos) {

    private var acceptingServerSocket: ServerSocket? = null
    private var serverJob: Job? = null

    val hostInstances: Flow<ServerInfo> = flow {
        while (true) {
            delay(500)
        }
    }

    suspend fun startServer(
        context: Context,
        discoveredHostSharedFlow: MutableSharedFlow<ClientInfo>,
        lostHostSharedFlow: MutableSharedFlow<ClientInfo>
    ) {
        serverJob = CoroutineScope(coroutineContext).launch {
            launch {
                startSendBroadcast(context)
                Log.i(TAG, "Broadcast coroutine leaving")
            }
            launch {
                startServerInfo(discoveredHostSharedFlow, lostHostSharedFlow)
                Log.i(TAG, "Server coroutine leaving")
            }
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

//        var socket: DatagramSocket? = null
        val socket = DatagramSocket()
        try {
            socket.broadcast = true

            val buffer = message.toByteArray()
            val packet =
                DatagramPacket(buffer, buffer.size, address, DISCOVERING_PORT)

            while (isActive) {
                socket.send(packet)
                Log.i(TAG, "Broadcast sent $address: $message")
                delay(1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Broadcast failed", e)
        } finally {
            socket.close()
            Log.e(TAG, "broadcast socket closed")
        }
    }

    private suspend fun startServerInfo(
        discoveredHostSharedFlow: MutableSharedFlow<ClientInfo>,
        lostHostSharedFlow: MutableSharedFlow<ClientInfo>
    ) = withContext(Dispatchers.IO) {
        Log.i(TAG, "Server starting on port $CONNECTION_PORT")

        acceptingServerSocket?.close()
        acceptingServerSocket = ServerSocket(CONNECTION_PORT)
        // TODO : create party / cancel / create party is crashing
        acceptingServerSocket?.let { serverSocket ->
//            try {
            while (isActive) {
                Log.i(TAG, "Waiting for incoming connections...")
                val clientSocket = serverSocket.accept()

                launch {
                    Log.i(TAG, "Client connected: ${clientSocket.inetAddress.hostAddress}")
                    handleClient(clientSocket, discoveredHostSharedFlow, lostHostSharedFlow)
                }
            }
            // TODO : the try catch is probably not usefull so it's commented, until we're sure
            // TODO : It's uesless
//            } catch (e: Exception) {
//                Log.e(TAG, "Server error: ${e.message}")
//                e.printStackTrace()
//            } finally {
            serverSocket.close()
            Log.i(TAG, "Server socket closed")
//            }
        }
    }

    private suspend fun handleClient(
        clientSocket: Socket,
        discoveredHostSharedFlow: MutableSharedFlow<ClientInfo>,
        lostHostSharedFlow: MutableSharedFlow<ClientInfo>
    ) = withContext(Dispatchers.IO) {
        Log.i(TAG, "Handling client")

        val comHelper = ComHelper(clientSocket)

        Log.i(TAG, "Waiting for client info...")
        val message = comHelper.readLineACK()
        Log.i(TAG, "Client info received: $message")

        try {
            val clientInfo = Json.decodeFromString<ClientInfo>(message)

            discoveredHostSharedFlow.emit(clientInfo)

            val infoInstance = InstanceInfo(
                name = "partie1",
                playersCount = 5,
                isLocked = false,
                primaryColor = Color.Blue.toArgb(),
                password = null
            )

            comHelper.writeLineACK(Json.encodeToString(infoInstance))
            launch {
                keepConnectedClient(clientInfo, comHelper, lostHostSharedFlow)
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error decoding client info: ${e.message} ${e.stackTraceToString()}"
            )
        } finally {
            clientSocket.close()
        }
    }

    private suspend fun keepConnectedClient(
        clientInfo: ClientInfo,
        comHelper: ComHelper,
        lostHostSharedFlow: MutableSharedFlow<ClientInfo>
    ) = withContext(Dispatchers.IO) {
        while (isActive) {
            delay(3000)
            // Ping calculable ici
            try {
                withTimeout(1000) {
                    comHelper.writeLineACK("Checking connection")
                }
            } catch (e: TimeoutCancellationException) {
                lostHostSharedFlow.emit(clientInfo)
            }
        }
    }

    override fun stopProcedures() {
        Log.i(TAG, "Stopping procedures")
        serverJob?.cancel()
    }
}