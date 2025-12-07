package com.velocitypulse.preums.play.network.discovery

import android.content.Context
import android.net.ConnectivityManager
import android.system.OsConstants
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

private const val TAG = "NetworkBase"

abstract class NetworkBase() {

    companion object {

        @JvmStatic
        protected val DISCOVERING_PORT = 8888
    }

    enum class EventState {
        NO_EVENT,
        PORT_FAILURE
    }

    abstract fun stopProcedures()

    protected fun getBroadcast(inet4Address: Inet4Address): InetAddress? {
        val temp: NetworkInterface
        var inetBroadcast: InetAddress? = null
        try {
            temp = NetworkInterface.getByInetAddress(inet4Address)
            val addresses = temp.interfaceAddresses
            for (inetAddress in addresses) inetBroadcast = inetAddress.broadcast
            return inetBroadcast
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }
}

fun getLocalIPv6(context: Context): Inet6Address? {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val props = manager.getLinkProperties(manager.activeNetwork)

    return props?.linkAddresses?.find {
        it.address is Inet6Address && it.flags == OsConstants.IFA_F_PERMANENT
    }?.address as Inet6Address?
}

fun getLocalIP(context: Context): Inet4Address? {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val props = manager.getLinkProperties(manager.activeNetwork)

    return props?.linkAddresses?.find {
        it.address is Inet4Address && it.flags == OsConstants.IFA_F_PERMANENT
    }?.address as Inet4Address?
}