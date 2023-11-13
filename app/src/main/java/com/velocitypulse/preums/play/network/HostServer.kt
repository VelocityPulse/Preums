package com.velocitypulse.preums.play.network

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.velocitypulse.preums.play.HostInstance
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

class HostServer {

    private val serverSocket = ServerSocket()

    private val receivedSockets = mutableListOf<Socket>()

    val hostInstances: Flow<HostInstance> = flow {
        while (true) {
            delay(500)
        }
    }

    suspend fun startServer(context: Context) {
        withContext(Dispatchers.IO) {

            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)

            val socket = serverSocket.accept()
            Log.d("debug", "accepted")

            val receiveChannel = socket.openReadChannel()

            while (true) {
                val text = receiveChannel.readUTF8Line(256)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, text, LENGTH_SHORT).show()
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