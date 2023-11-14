package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.Socket

class HostClient : Host() {
    private val clientSocket = Socket()

    suspend fun startClient(context: Context) {
        withContext(Dispatchers.IO) {
            while (true) {
                delay(500)
                try {
                    val selectorManager = SelectorManager(Dispatchers.IO)
                    val socket = aSocket(selectorManager).tcp().connect(
//                        getBroadcast(getLocalIP(context)!!)!!.hostName,
                        "0.0.0.0",
                        59002
                    )

                    val sendChannel = socket.openWriteChannel(autoFlush = true)

                    sendChannel.writeStringUtf8("asking connection\n")

                    Log.d("debug", "opening read line")
                    socket.openReadChannel().readUTF8Line()
                    Log.d("debug", "FAIL -> has passed read")


                } catch (e: ConnectException) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun stopServer() {
        withContext(Dispatchers.IO) {
            clientSocket.close()
        }
    }

}