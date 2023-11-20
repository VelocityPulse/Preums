package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.velocitypulse.preums.play.HostInfo
import com.velocitypulse.preums.play.HostInstance
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.net.ServerSocket
import java.net.Socket
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo
import javax.net.ssl.SSLServerSocketFactory


class HostServer : Host() {

    private val serverSocket = ServerSocket()

    private val receivedSockets = mutableListOf<Socket>()


    val hostInstances: Flow<HostInstance> = flow {
        while (true) {
            delay(500)
        }
    }

    suspend fun startServer(context: Context) {
        startServerMDNS(context)
        startGameServer(context)
    }

    suspend fun startServerMDNS(context: Context) {
        withContext(Dispatchers.IO) {
            // Create a JmDNS instance
            val jmdns = JmDNS.create("192.168.1.49")

            // Register a service
            val serviceInfo: ServiceInfo = ServiceInfo.create(
                "_http._tcp.local.", "SUCCEED", CONNECTION_PORT, context.packageName
            )
            jmdns.registerService(serviceInfo)
        }
    }

    private suspend fun startGameServer(context: Context) {
        withContext(Dispatchers.IO) {
            Log.d("debug", "Starting game server")
            val serverSocketFactory = getSecuredSocketFactory()
            // TODO : recréer getSecuredSocketFactory mais qui retourne serversocketfactory au lieu
                // de socketfactory
            val serverSocket = serverSocketFactory.createServerSocket(CONNECTION_PORT)
            val clientSocket = serverSocket.accept()
            Log.d("debug", "Contact accepted")

            val receiveChannel =  clientSocket.openReadChannel()
            val sendChannel = clientSocket.openWriteChannel()

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
}
