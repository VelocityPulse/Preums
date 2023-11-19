package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo


class HostServer : Host() {

    private val serverSocket = ServerSocket()

    private val receivedSockets = mutableListOf<Socket>()


    val hostInstances: Flow<HostInstance> = flow {
        while (true) {
            delay(500)
        }
    }

    suspend fun startServer(context: Context) {
//        startServerBroadcast(context)
        startServerMDNS(context)
    }

    suspend fun startServerMDNS(context: Context) {
        withContext(Dispatchers.IO) {
            // Create a JmDNS instance
            val jmdns = JmDNS.create("192.168.1.49")

            // Register a service
            val serviceInfo: ServiceInfo =
                ServiceInfo.create("_http._tcp.local.", "SUCCEED", 1234, "path=index.html")
            jmdns.registerService(serviceInfo)
        }
    }

    suspend fun startServerBroadcast(context: Context) {

        withContext(Dispatchers.IO) {

            val selectorManager = SelectorManager(Dispatchers.IO)
            // TODO ip !! warning
            val serverSocket = aSocket(selectorManager).tcp().bind(
                "255.255.255.255", DISCOVERING_PORT
            )
            Log.d("debug", "accepting")
            val socketCandidate = serverSocket.accept()

            val receiveChannel = socketCandidate.openReadChannel()
            val sendChannel = socketCandidate.openWriteChannel(autoFlush = true)

            if (receiveChannel.readUTF8Line(30) == "asking for discovering") {
                getLocalIP(context)?.hostAddress?.let { localIp ->

                    Log.d("debug", "retrieved ip $localIp")

                    val testHostInstance = HostInstance(
                        ip = localIp,
                        port = CONNECTION_PORT,
                        name = "testInstanceName",
                        playersCount = 5,
                        isLocked = true,
                        password = "root",
                        primaryColor = Color.Cyan.toArgb()
                    )

                    sendChannel.writeLine(testHostInstance.ip)
                    sendChannel.writeLine(testHostInstance.port.toString())
                    sendChannel.writeLine(testHostInstance.name)
                    sendChannel.writeLine(testHostInstance.playersCount.toString())
                    sendChannel.writeLine(testHostInstance.isLocked.toString())
                    sendChannel.writeLine(testHostInstance.primaryColor.toString())

                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, localIp, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    suspend fun stopServer() {
        withContext(Dispatchers.IO) {
            serverSocket.close()
        }
    }
}