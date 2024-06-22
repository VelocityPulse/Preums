package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.wifi.WifiManager
import android.os.StrictMode
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.velocitypulse.preums.play.HostInfo
import com.velocitypulse.preums.play.HostInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo
import javax.net.ssl.SSLServerSocket

class HostServer : Host() {

    private val serverSocket = ServerSocket()

    private val receivedSockets = mutableListOf<Socket>()


    val hostInstances: Flow<HostInstance> = flow {
        while (true) {
            delay(500)
        }
    }

    fun startServer(context: Context) {
/*
        Thread {
            val addresses = InetAddress.getAllByName("192.168.1.169")
            for (address in addresses) {
                if (address.isReachable(10000)) {
                    Log.d("debugPreums", "Connected $address")
                } else {
                    Log.d("debugPreums", "Failed $address")
                }
            }
            Log.d("debugPreums", "Finished isReachable")
        }.start()
*/

        // BROADCAST 1
        // Huawei : 192.168.1.255
        // Nothing : 192.168.1.255
        // Smasung A20E : 192.168.1.255
        // Samsung S20FE : 192.168.1.255
        val address1 = getBroadcast(getLocalIP(context)!!)!!
        Log.d("debugPreums", "getting address from local IP and active network: ${address1.hostAddress}")
        sendBroadcast("hello hello it's brdcast 1", address1, context)

        // BROADCAST 2
        // Huawei : 192.168.1.255
        // Nothing : 255.255.255.255
        // Samsung A20E : 255.255.255.255
        // Samsung S20FE : 255.255.255.255
        val address2 = getBroadcastAddress(context)
        Log.d("debugPreums", "getting address from DHCP: \t\t\t\t\t${address2.hostAddress}")
        sendBroadcast("hello hello it's brdcast 2", address2, context)

        // BROADCAST 3
        val address3 = Inet4Address.getByName("192.168.1.255")
        Log.d("debugPreums", "getting address from DHCP: \t\t\t\t\t${address3.hostAddress}")
        sendBroadcast("hello hello it's brdcast 3", address3, context, true)

        // BROADCAST 4
        val address4 = Inet4Address.getByName("255.255.255.255")
        Log.d("debugPreums", "getting address from local IP and active network: ${address4.hostAddress}")
        sendBroadcast("hello hello it's brdcast 4", address4, context, true)

        // BROADCAST 5
        val address5 = Inet4Address.getByName("192.168.1.255")
        Log.d("debugPreums", "getting address from DHCP: \t\t\t\t\t${address5.hostAddress}")
        sendBroadcast("hello hello it's brdcast 5", address5, context, false)

        // BROADCAST 6
        val address6 = Inet4Address.getByName("255.255.255.255")
        Log.d("debugPreums", "getting address from local IP and active network: ${address6.hostAddress}")
        sendBroadcast("hello hello it's brdcast 6", address6, context, false)


//        startServerMDNS(context)
//        startGameServer(context)
    }

    fun sendBroadcast(message: String, address: InetAddress, context: Context, policy: Boolean = true) {

//        Thread {

        if (policy) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket()
            socket.broadcast = true // No impact if not set, but disables it if set to false

//            Log.d("UDP", "datagramsocket broadcast: " + socket.broadcast)

//            Log.d("UDP", "My adress : " + getLocalIP(context)!!.hostAddress)

//            Log.d("UDP", "Broadcast adresse : " + getBroadcast(getLocalIP(context)!!))

            val buffer = message.toByteArray()
//                InetAddress.getByName("255.255.255.255") // Adresse de broadcast
            val packet =
                DatagramPacket(buffer, buffer.size, address, 8888) // Port 8888

//                while (true) {
            socket.send(packet)
//                    sleep(1000)
//                }
            Log.d("UDP", "Broadcast sent: $message")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("UDP", "Broadcast failed", e)
        } finally {
            if (socket != null && !socket.isClosed) {
                socket.close()
            }
        }
//        }.start()
    }

    suspend fun startServerMDNS(context: Context) {
        withContext(Dispatchers.IO) {
            // Create a JmDNS instance
//            val jmdns = JmDNS.create("192.168.1.49")
            val jmdns = JmDNS.create(getLocalIP(context))

            // Register a service
            val serviceInfo: ServiceInfo = ServiceInfo.create(
                "_http._tcp.local.", "SUCCEED", CONNECTION_PORT, context.packageName
            )
            jmdns.registerService(serviceInfo)
        }
    }

    private suspend fun startGameServer(context: Context) {

        withContext(Dispatchers.IO) {
            Log.d("debug", "Starting game server")
            val serverSocketFactory = getSecuredSocketFactory(context).serverSocketFactory
            val serverSocket =
                serverSocketFactory.createServerSocket(CONNECTION_PORT) as SSLServerSocket
            serverSocket.enabledCipherSuites = serverSocket.supportedCipherSuites
            serverSocket.needClientAuth = true
            serverSocket.enabledProtocols = listOf("TLSv1.2").toTypedArray()
            Log.d("debug", "trying to accept contact")
            val clientSocket = serverSocket.accept()
            Log.d("debug", "Contact accepted")

            // TODO :
            // quand le serveur et le client ouvrent leurs channel, ca crash
            // verifier si les certificats sont bien ajoutés
            // par exemple dans le turst store ou autre

            val receiveChannel = clientSocket.openReadChannel()
            /*
                        val receiveChannel = clientSocket.getInputStream().let {
                            Log.d("debug", "a")
                            BufferedInputStream(it, 1024).let {
                                Log.d("debug", "b")
                                InputStreamReader(it).let {
                                    Log.d("debug", "c")
                                    BufferedReader(it)
                                }
                            }
                        }
                        Log.d("debug", "received " + receiveChannel.readLine())
            */

            Log.d("debug", "receive channel opened")


            /*            val sendChannel = BufferedWriter(
                            OutputStreamWriter(
                                BufferedOutputStream(
                                    clientSocket.getOutputStream(), 1024
                                )
                            )
                        )*/


            val sendChannel = clientSocket.openWriteChannel()
            Log.d("debug", "send channel opened")


            val infoInstance = HostInfo(
                name = "partie1",
                playersCount = 5,
                isLocked = false,
                primaryColor = Color.Blue.toArgb(),
                password = null
            )

            sendChannel.writeLine(infoInstance.name)
            sendChannel.writeLine(infoInstance.playersCount.toString())
            sendChannel.writeLine(infoInstance.isLocked.toString())
            sendChannel.writeLine(infoInstance.primaryColor.toString())
            Log.d("debug", "Info sent")
        }
    }

    override fun stopProcedures() {
    }
}