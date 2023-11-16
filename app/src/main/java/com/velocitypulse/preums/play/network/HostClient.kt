package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.velocitypulse.preums.play.HostInstance
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.Socket

class HostClient : Host() {
    private val clientSocket = Socket()

    private var discoveringJob: Job? = null
    private var connectionJob: Job? = null

    suspend fun startClient(context: Context) {
        withContext(Dispatchers.IO) {

            discoveringJob?.cancel()
            discoveringJob = CoroutineScope(coroutineContext).launch {
                startDiscovering(context)
            }

//            connectServer()

        }
    }


    private suspend fun connectServer(context: Context) = coroutineScope {
        delay(500)


    }

    private suspend fun startDiscovering(context: Context) = coroutineScope {
        delay(500)
        try {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect(
                "0.0.0.0", 59002
            )

            val sendingChannel = socket.openWriteChannel(autoFlush = true)
            sendingChannel.writeStringUtf8("asking for discovering\n")

            val receivingChannel = socket.openReadChannel()

            val receivedTestHostInstance = HostInstance(
                ip = receivingChannel.readUTF8Line()!!,
                port = receivingChannel.readUTF8Line()!!.toInt(),
                name = receivingChannel.readUTF8Line()!!,
                playersCount = receivingChannel.readUTF8Line()!!.toInt(),
                isLocked = receivingChannel.readUTF8Line()!!.toBoolean(),
                password = null,
                primaryColor = receivingChannel.readUTF8Line()!!.toInt()
            )

            Log.d("debug", receivedTestHostInstance.toString())

            withContext(Dispatchers.Main) {
                Toast.makeText(context, receivedTestHostInstance.toString(), Toast.LENGTH_LONG).show()
            }


        } catch (e: ConnectException) {
            e.printStackTrace()
        }
    }

    suspend fun stopServer() {
        withContext(Dispatchers.IO) {
            clientSocket.close()
        }
    }

}