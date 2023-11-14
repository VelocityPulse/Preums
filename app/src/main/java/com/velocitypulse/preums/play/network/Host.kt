package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.system.OsConstants
import android.util.Log
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException


abstract class Host {
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
}