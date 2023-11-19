package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.Socket
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class HostClient : Host() {
    private val clientSocket = Socket()

    private var discoveringJob: Job? = null
    private var connectionJob: Job? = null

    val discoveredParties = flowOf<HostInstance>()

    @Deprecated("rename start discovering")
    suspend fun startClient(context: Context) {
        withContext(Dispatchers.IO) {

            discoveringJob?.cancel()
            discoveringJob = CoroutineScope(coroutineContext).launch {
//                startDiscoveringBroadcast(context).collect()
                startDiscoveringMDNS(context).collect()
            }
//            connectServer()
        }
    }

    private suspend fun startDiscoveringMDNS(context: Context): Flow<HostInstance> = flow {
// Exemple (l'utilisation réelle peut varier selon la bibliothèque mDNS utilisée)
        val jmdns = JmDNS.create()

        val listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                    Log.d("debug", "Service added: " + event.info);
            }

            override fun serviceRemoved(event: ServiceEvent) {
//                    Log.d("debug", "Service removed : " + event.info);
            }

            override fun serviceResolved(event: ServiceEvent) {
                Log.d("debug", "Service resolved : " + event.info);
            }
        }
        jmdns.addServiceListener("_http._tcp.local.", listener)
        while (currentCoroutineContext().isActive) {
            delay(500)
        }
    }


    private suspend fun connectServer(context: Context) = coroutineScope {
        delay(500)
    }

    private suspend fun startDiscoveringBroadcast(context: Context): Flow<HostInstance> = flow {
        while (currentCoroutineContext().isActive) {
            delay(500)
            try {
                val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
                val networkInfo = connectivityManager!!.activeNetworkInfo

                if (networkInfo == null || !networkInfo.isConnected) {
                    throw Exception("PAS DE RESEAU")
                }

                val selectorManager = SelectorManager(Dispatchers.IO)
                val socket = aSocket(selectorManager).tcp().connect(
                    "255.255.255.255", DISCOVERING_PORT
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
                    Toast.makeText(context, receivedTestHostInstance.name, Toast.LENGTH_LONG).show()
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
}