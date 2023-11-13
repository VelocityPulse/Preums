package com.velocitypulse.preums.play.network

import android.util.Log
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.Socket

class HostClient {
    private val clientSocket = Socket()

    suspend fun startClient() {
        withContext(Dispatchers.IO) {

            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect("127.0.0.1", 9002)

            val sendChannel = socket.openWriteChannel(autoFlush = true)

            repeat(50) {
                Log.d("debug", "sending")
                sendChannel.writeStringUtf8("test $it\n")
                delay(5000)
            }
        }
    }

    suspend fun stopServer() {
        withContext(Dispatchers.IO) {
            clientSocket.close()
        }
    }

}