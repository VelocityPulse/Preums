package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import android.widget.Toast
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


class HostServer: Host() {

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

            val ip = getLocalIP(context)
            Log.d("debug", "retrieved ip ${ip?.hostName}")

            val broadcast = getBroadcast(ip!!)
            Log.d("debug", "retrieved broadcast ${broadcast!!.hostName}")

            val selectorManager = SelectorManager(Dispatchers.IO)
            // TODO ip !! warning
//            val serverSocket = aSocket(selectorManager).tcp().bind(ip!!.hostName, 59002)
//            val serverSocket = aSocket(selectorManager).tcp().bind(broadcast!!.hostName, 59002)
            val serverSocket = aSocket(selectorManager).tcp().bind("0.0.0.0", 59002)
            val socketCandidate = serverSocket.accept()

            val receiveChannel = socketCandidate.openReadChannel()

            if (receiveChannel.readUTF8Line(30) == "asking connection") {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "requesting connection", Toast.LENGTH_LONG).show()
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