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
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo
import javax.net.ssl.SSLServerSocket


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
//            val jmdns = JmDNS.create("192.168.1.49")
            val jmdns = JmDNS.create(getLocalIP(context))

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
            val serverSocketFactory = getSecuredSocketFactory(context).serverSocketFactory
            val serverSocket = serverSocketFactory.createServerSocket(CONNECTION_PORT) as SSLServerSocket
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

            val receiveChannel =  clientSocket.openReadChannel()
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
}
