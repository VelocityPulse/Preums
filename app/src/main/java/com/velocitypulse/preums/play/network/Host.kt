package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.system.OsConstants
import android.util.Log
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.random.Random


abstract class Host {

    companion object {
        @JvmStatic
//        protected val DISCOVERING_IP = "0.0.0.0"
        protected val DISCOVERING_IP = "192.168.0.255"

        @JvmStatic
        protected val DISCOVERING_PORT = 59002

        @JvmStatic
        protected val CONNECTION_PORT = Random.nextInt(from = 49151, until = 65534)
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

    suspend fun ByteWriteChannel.writeLine(message: String) {
        writeStringUtf8(message + "\n")
    }
}