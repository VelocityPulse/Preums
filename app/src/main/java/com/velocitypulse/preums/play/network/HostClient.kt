package com.velocitypulse.preums.play.network

import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.velocitypulse.preums.play.HostInfo
import com.velocitypulse.preums.play.HostInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class HostClient : Host() {
//    private val clientSocket = Socket()

    private var discoveringJob: Job? = null
    private var connectionJob: Job? = null

    val discoveredParties = flowOf<HostInstance>()

    suspend fun startDiscovering(context: Context) {

        Thread { receiveBroadcast(InetAddress.getByName("0.0.0.0"), false) }.start()
        Thread { receiveBroadcast(getBroadcast(getLocalIP(context)!!)!!, false) }.start()
        Thread { receiveBroadcast(getBroadcastAddress(context), false) }.start()

        Thread { receiveBroadcast(InetAddress.getByName("0.0.0.0"), true) }.start()
        Thread { receiveBroadcast(getBroadcast(getLocalIP(context)!!)!!, true) }.start()
        Thread { receiveBroadcast(getBroadcastAddress(context), true) }.start()

        lockBroadcast(context)
        Thread { receiveBroadcast(InetAddress.getByName("0.0.0.0"), false) }.start()
        Thread { receiveBroadcast(getBroadcast(getLocalIP(context)!!)!!, false) }.start()
        Thread { receiveBroadcast(getBroadcastAddress(context), false) }.start()

        Thread { receiveBroadcast(InetAddress.getByName("0.0.0.0"), true) }.start()
        Thread { receiveBroadcast(getBroadcast(getLocalIP(context)!!)!!, true) }.start()
        Thread { receiveBroadcast(getBroadcastAddress(context), true) }.start()


        /*        withContext(Dispatchers.IO) {

                    discoveringJob?.cancel()
                    discoveringJob = CoroutineScope(coroutineContext).launch {
                        startDiscoveringMDNS(context).collect() {
                            Log.d("Debug", "got instance: $it")
                            contactServer(context, it)
                        }
                    }
                }*/
    }

    fun receiveBroadcast(inetAddress: InetAddress, threadPolicy: Boolean) {

        if (threadPolicy) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        Log.d("debugPreums", "Receiving broadcast...")
        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket(8888, inetAddress) // Use the same port as the sender
            socket.broadcast = true

            while (true) {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)
                Log.d("debugPreums", "Receive packet")
                socket.receive(packet)

                val message =
                    String(packet.data, 0, packet.length)
                Log.d("debugPreums", "Broadcast received: $message")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("debugPreums", "Receiving broadcast failed", e)
        } finally {
            if (socket != null && !socket.isClosed) {
                socket.close()
            }
        }
    }

    private suspend fun startDiscoveringMDNS(context: Context): Flow<HostInstance> = callbackFlow {
        val jmdns = JmDNS.create()
        Log.d("debug", "starting MDNS init")

        val listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                Log.d("debug", "Service added: " + event.info)
            }

            override fun serviceRemoved(event: ServiceEvent) {
            }

            override fun serviceResolved(event: ServiceEvent) {
                if (event.info.hasData() && event.info.textBytes.decodeToString()
                        .contains(context.packageName)
                ) {
                    Log.d("debug", "Service resolved : $event")
                    trySend(
                        HostInstance(
                            ip = event.info.inet4Addresses.first().hostName,
                            port = event.info.port,
                            info = null
                        )
                    )
                }
            }
        }
        jmdns.addServiceListener("_http._tcp.local.", listener)
        while (currentCoroutineContext().isActive) {
            delay(500)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun contactServer(context: Context, hostInstance: HostInstance) =
        coroutineScope {
            launch(Dispatchers.IO) {

                val socketFactory = getSecuredSocketFactory(context).socketFactory
                socketFactory.createSocket()/*.use */.let { socket -> // TODO : put use

                    Log.d("debug", "Starting contact socket")
                    // TODO : handle ConnectException
                    socket.connect(InetSocketAddress(hostInstance.ip, hostInstance.port))
                    Log.d("debug", "Contact socket connected")

                    val receiveChannel = socket.openReadChannel()
                    Log.d("debug", "receive channel opened")

                    val sendChannel = socket.openWriteChannel()
                    Log.d("debug", "send channel opened")

                    Log.d("debug", "reading one debug line : " + receiveChannel.readLine())

//                    sendChannel.writeLine("test")

//                    while(true) delay(500)

                    val hostInfo = HostInfo(
                        name = receiveChannel.readLine()!!,
                        playersCount = receiveChannel.readLine()!!.toInt(),
                        isLocked = receiveChannel.readLine()!!.toBoolean(),
                        password = null,
                        primaryColor = receiveChannel.readLine()!!.toInt()
                    )

//                    Log.d("debug", "Contact established: $hostInfo")
                }
            }
        }

    override fun stopProcedures() {
    }
}