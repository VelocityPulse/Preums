package com.velocitypulse.preums.play.network

import android.content.Context
import android.util.Log
import com.velocitypulse.preums.play.HostInfo
import com.velocitypulse.preums.play.HostInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.security.KeyStore
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

class HostClient : Host() {
//    private val clientSocket = Socket()

    private var discoveringJob: Job? = null
    private var connectionJob: Job? = null

    val discoveredParties = flowOf<HostInstance>()

    @Deprecated("rename start discovering")
    suspend fun startClient(context: Context) {
        withContext(Dispatchers.IO) {

            discoveringJob?.cancel()
            discoveringJob = CoroutineScope(coroutineContext).launch {
                startDiscoveringMDNS(context).collect() {
                    Log.d("Debug", "got instance: $it")
                    contactServer(context, it)
                }
            }
        }
    }

    private suspend fun startDiscoveringMDNS(context: Context): Flow<HostInstance> = callbackFlow {
        val jmdns = JmDNS.create()

        val listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent) {
                Log.d("debug", "Service added: " + event.info);
            }

            override fun serviceRemoved(event: ServiceEvent) {
            }

            override fun serviceResolved(event: ServiceEvent) {
                if (event.info.hasData() && event.info.textBytes.decodeToString()
                        .contains(context.packageName)) {
                    Log.d("debug", "Service resolved : $event");
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
    }

    private suspend fun contactServer(context: Context, hostInstance: HostInstance) =
        coroutineScope {
            launch(Dispatchers.IO) {

                val keyStore = getKeyStore()
                val sslContext = SSLContext.getInstance(SSL_PROTOCOL)
                val kmf = KeyManagerFactory.getInstance(KEY_ALGORITHM)
                val keyStorePassphrase = KEY_PASS.toCharArray()

                kmf.init(keyStore, keyStorePassphrase)

                val tmf = TrustManagerFactory.getInstance(KEY_ALGORITHM)
                tmf.init(keyStore)

                sslContext.init(kmf.keyManagers, tmf.trustManagers, null)

                val socketFactory = getSecuredSocketFactory()
                socketFactory.createSocket().use { socket ->

                    Log.d("debug", "Starting contact socket")
                    socket.connect(InetSocketAddress(hostInstance.ip, hostInstance.port))
                    Log.d("debug", "Contact socket connected")

                    val receiveChannel = socket.openReadChannel()
                    val sendChannel = socket.openWriteChannel()

                    val hostInfo = HostInfo(
                        name = receiveChannel.readLine()!!,
                        playersCount = receiveChannel.readLine()!!.toInt(),
                        isLocked = receiveChannel.readLine()!!.toBoolean(),
                        password = null,
                        primaryColor = receiveChannel.readLine()!!.toInt()
                    )

                    Log.d("debug", "Contact established: $hostInfo")
                }
            }
        }
}