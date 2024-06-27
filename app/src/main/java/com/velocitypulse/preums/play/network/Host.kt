package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.system.OsConstants
import android.util.Log
import com.google.android.gms.security.ProviderInstaller
import com.velocitypulse.preums.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import kotlin.jvm.Throws

abstract class Host {

    companion object {

        @JvmStatic
        protected val KEY_NAME = "keystore.jks"

        @JvmStatic
        protected val KEY_PASS = "123456"

        @JvmStatic
        protected val KEY_INSTANCE_TYPE = "PKCS12"

        @JvmStatic
        protected val SSL_PROTOCOL = "TLS"

        @JvmStatic
        protected val KEY_ALGORITHM = "SunX509"

        @JvmStatic
        protected val DISCOVERING_PORT = 8888
    }

    protected fun getLocalIPv6(context: Context): Inet6Address? {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val props = manager.getLinkProperties(manager.activeNetwork)

        return props?.linkAddresses?.find {
            it.address is Inet6Address && it.flags == OsConstants.IFA_F_PERMANENT
        }?.address as Inet6Address?
    }

    protected fun getLocalIP(context: Context): Inet4Address? {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val props = manager.getLinkProperties(manager.activeNetwork)

        return props?.linkAddresses?.find {
            it.address is Inet4Address && it.flags == OsConstants.IFA_F_PERMANENT
        }?.address as Inet4Address?
    }

    abstract fun stopProcedures()

    protected fun getKeyStore(context: Context): KeyStore {
        val keyStore = KeyStore.getInstance("BKS")
//        val keyStreamFd = context.resources.openRawResourceFd(R.raw.servercertbks)
//        val keyStream = FileInputStream(keyStreamFd.fileDescriptor)
        val keyStream = context.resources.openRawResource(R.raw.servercertbks)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Log.d("debug", keyStream.readAllBytes().contentToString())
//        }

        keyStore.load(keyStream, KEY_PASS.toCharArray())

        return keyStore
    }

    protected fun getSecuredSocketFactory(context: Context): SSLContext {
        ProviderInstaller.installIfNeeded(context)

        val keyStore = getKeyStore(context)
        return SSLContext.getInstance("TLS").apply {
            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmf.init(keyStore, KEY_PASS.toCharArray())

            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)

            init(kmf.keyManagers, tmf.trustManagers, SecureRandom.getInstanceStrong())
//            init(kmf.keyManagers, null, null)
        }
    }

    protected fun getBroadcast(inet4Address: Inet4Address): InetAddress? {
        val temp: NetworkInterface
        var inetBroadcast: InetAddress? = null
        try {
            temp = NetworkInterface.getByInetAddress(inet4Address)
            val addresses = temp.interfaceAddresses
            for (inetAddress in addresses) inetBroadcast = inetAddress.broadcast
            Log.d("debug", "iAddr=$inetBroadcast")
            return inetBroadcast
        } catch (e: SocketException) {
            e.printStackTrace()
            Log.d("debug", "getBroadcast" + e.message)
        }
        return null
    }


//    protected fun BufferedWriter.writeLineAcknowledged(timeout: Long = 1000, message: String) {
//        do {
//            sendChannel.writeLine(infoMessage)
//        } while (receiveChannel.waitAcknowledge())
//    }
//
//    protected suspend fun BufferedReader.waitAcknowledge(timeout: Long = 1000): Boolean {
//        return withTimeout(timeout) {
//            readlnOrNull() == "ACK"
//        }
//    }
//
//    protected suspend fun BufferedWriter.sendAcknowledgment() {
//        writeLine("ACK")
//    }

    protected open class ComHelper(socket: Socket) {

        companion object {
            private const val ACKNOWLEDGE = "ACK"
        }

        private val receiveChannel: BufferedReader = socket.openReadChannel()
        private val sendChannel: BufferedWriter = socket.openWriteChannel()

        private fun Socket.openReadChannel(): BufferedReader {
            return this.getInputStream().bufferedReader()
        }

        private fun Socket.openWriteChannel(): BufferedWriter {
            return this.getOutputStream().bufferedWriter()
        }

        fun BufferedWriter.writeLine(message: String) {
            write(message + System.lineSeparator())
        }

        private suspend fun BufferedReader.waitAcknowledged(timeout: Long): Boolean {
            try {
                return withTimeout(timeout) {
                    while (isActive) {
                        if (this@waitAcknowledged.ready() && this@waitAcknowledged.readLine() == ACKNOWLEDGE) {
                            return@withTimeout true
                        }
                        delay(50)
                    }
                    false
                }.also {
                    Log.d("preumsDebug", "Leaving acknowlegment check with $it")
                }
            } catch (e: Exception) {
//                Log.d("preumsDebug", "Leaving waitAcknowledged ${e.message}")
            }
            return false
        }

        suspend fun writeLineAcknowledged(message: String, sendingFrequency: Long = 1000) {
            do {
                Log.d("preumsDebug", "sending acknowledged [$message]")
                sendChannel.writeLine(message)
            } while (!receiveChannel.waitAcknowledged(sendingFrequency))
        }

        suspend fun readLineAcknowledged(): String {
            return withContext(Dispatchers.IO) {
                receiveChannel.readLine().also {
                    sendChannel.writeLine(ACKNOWLEDGE)
                }
            }
        }
    }

}












