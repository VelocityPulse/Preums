package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.system.OsConstants
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.security.KeyStore
import javax.net.SocketFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import kotlin.random.Random


abstract class Host {

    companion object {

        @JvmStatic
        protected val KEY_NAME = "keystore.jks"

        @JvmStatic
        protected val KEY_PASS = "eNCGfxUGH8yJPUi"

        @JvmStatic
        protected val KEY_INSTANCE_TYPE = "PKCS12"

        @JvmStatic
        protected val SSL_PROTOCOL = "TLS"

        @JvmStatic
        protected val KEY_ALGORITHM = "SunX509"


        @JvmStatic
        protected val DISCOVERING_IP = "192.168.0.255"

        @JvmStatic
        protected val DISCOVERING_PORT = 59002

        @JvmStatic
        protected val CONNECTION_PORT = Random.nextInt(from = 49151, until = 65534)
    }

    protected fun getKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KEY_INSTANCE_TYPE)
        val keyStream = javaClass.classLoader?.getResourceAsStream(KEY_NAME)

        keyStore.load(keyStream, KEY_PASS.toCharArray())

        return keyStore
    }

    protected fun getSecuredSocketFactory(): SocketFactory {
        val keyStore = getKeyStore()
        return SSLContext.getInstance(SSL_PROTOCOL).apply {
            val kmf = KeyManagerFactory.getInstance(KEY_ALGORITHM)
            val keyStorePassphrase = KEY_PASS.toCharArray()

            kmf.init(keyStore, keyStorePassphrase)

            val tmf = TrustManagerFactory.getInstance(KEY_ALGORITHM)
            tmf.init(keyStore)

            init(kmf.keyManagers, tmf.trustManagers, null)
        }.socketFactory
    }

    protected fun getLocalIP(context: Context): Inet4Address? {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val props = manager.getLinkProperties(manager.activeNetwork)

        return props?.linkAddresses?.find {
            it.address is Inet4Address && it.flags == OsConstants.IFA_F_PERMANENT
        }?.address as Inet4Address?
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

    fun BufferedWriter.writeLine(message: String) {
        write(message + System.lineSeparator())
    }

    fun Socket.openReadChannel(): BufferedReader {
        return this.getInputStream().bufferedReader()
    }


    fun Socket.openWriteChannel(): BufferedWriter {
        return this.getOutputStream().bufferedWriter()
    }
}