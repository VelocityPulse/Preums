package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.velocitypulse.preums.play.HostInstance
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random


class HostServer : Host() {

    companion object {

        private const val DISCOVERING_IP = "0.0.0.0"
        private const val DISCOVERING_PORT = 59002
        private val CONNECTION_PORT = Random.nextInt(from = 49151, until = 65534)
    }

    private val serverSocket = ServerSocket()

    private val receivedSockets = mutableListOf<Socket>()


    val hostInstances: Flow<HostInstance> = flow {
        while (true) {
            delay(500)
        }
    }

    suspend fun startServer(context: Context) {

        // TODO : is wifi enabled ?

        withContext(Dispatchers.IO) {

            val selectorManager = SelectorManager(Dispatchers.IO)
            // TODO ip !! warning
            val serverSocket = aSocket(selectorManager).tcp().bind(DISCOVERING_IP, DISCOVERING_PORT)
            val socketCandidate = serverSocket.accept()

            val receiveChannel = socketCandidate.openReadChannel()
            val sendChannel = socketCandidate.openWriteChannel(autoFlush = true)

            if (receiveChannel.readUTF8Line(30) == "asking for discovering") {
                getLocalIP(context)?.hostAddress?.let { localIp ->

                    Log.d("debug", "retrieved ip $localIp")

                    val testHostInstance = HostInstance(
                        ip = localIp,
                        port = CONNECTION_PORT,
                        name = "testInstance",
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
                        Toast.makeText(context, localIp, Toast.LENGTH_LONG).show()
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