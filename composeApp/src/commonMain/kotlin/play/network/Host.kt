package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.system.OsConstants
import android.util.Log
import com.google.android.gms.security.ProviderInstaller
import com.velocitypulse.preums.R
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileDescriptor
import java.io.FileInputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import kotlin.jvm.JvmStatic
import kotlin.random.Random


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
        protected val DISCOVERING_IP = "192.168.0.255"

        @JvmStatic
        protected val DISCOVERING_PORT = 59002

        @JvmStatic
        protected val CONNECTION_PORT = Random.nextInt(from = 49151, until = 65534)
    }

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
        ProviderInstaller.installIfNeeded(context);

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


