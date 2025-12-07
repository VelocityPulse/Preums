package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.system.OsConstants
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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

private const val TAG = "Host"

abstract class Host() {

    companion object {

        @JvmStatic
        protected val DISCOVERING_PORT = 8888
    }

    enum class EventState {
        NO_EVENT,
        PORT_FAILURE
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

    protected open class NetHelper(val socket: Socket) {

        companion object {
            private const val ACKNOWLEDGE = "ACK"
            private const val TAG = "NetHelper"
        }

        private val receiveChannel: BufferedReader = socket.openReadChannel()
        private val sendChannel: BufferedWriter = socket.openWriteChannel()

        init {
            Log.i(TAG, "With socket: $socket")
        }

        private fun Socket.openReadChannel(): BufferedReader {
            return this.getInputStream().bufferedReader()
        }

        private fun Socket.openWriteChannel(): BufferedWriter {
            return this.getOutputStream().bufferedWriter()
        }

        private fun BufferedWriter.writeLine(message: String) {
            write(message + System.lineSeparator())
            flush()
        }

        private suspend fun BufferedReader.waitAcknowledged(timeout: Long): Boolean {
            return try {
                withTimeout(timeout) {
                    while (isActive) {
                        if (this@waitAcknowledged.ready()) {
                            val line = this@waitAcknowledged.readLine()
                            if (line == ACKNOWLEDGE) {
                                Log.i(TAG, "Acknowledgement received")
                                return@withTimeout true
                            } else {
                                Log.e(TAG, "Not acknowledged, received $line")
                            }
                        } else {
                            Log.i(TAG, "Waiting ACK...")
                        }
                        delay(100)
                    }
                    Log.i(TAG, "Timeout reached without acknowledgement")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in waitAcknowledged: ${e.stackTraceToString()}")
                false
            }
        }

        suspend fun writeLineACK(message: String, sendingFrequency: Long = 1000) =
            withContext(Dispatchers.IO) {
                do {
                    sendChannel.writeLine(message)
                } while (!receiveChannel.waitAcknowledged(sendingFrequency))
            }

        suspend fun readLineACK(): String = withContext(Dispatchers.IO) {
            while (!receiveChannel.ready()) {
                delay(50)
            }

            receiveChannel.readLine().also {
                sendChannel.writeLine(ACKNOWLEDGE)
            }
        }
    }
}
