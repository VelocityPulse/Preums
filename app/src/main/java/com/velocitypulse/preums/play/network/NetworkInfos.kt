package com.velocitypulse.preums.play.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "NetworkInfos"

class NetworkInfos(val context: Context) {

    private val _wifiNetworkAvailable = MutableStateFlow<Network?>(null)
    val wifiNetworkAvailable: StateFlow<Network?> get() = _wifiNetworkAvailable

    init {
        surveyNetwork()
    }

    private fun surveyNetwork() {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

        connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.e(TAG, "The default network is now: $network")
                    updateNetworkStatus(connectivityManager)
                }

                override fun onLost(network: Network) {
                    Log.e(
                        TAG,
                        "The application no longer has a default network. The last default network was $network"
                    )
                    updateNetworkStatus(connectivityManager)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    Log.e(TAG, "The default network changed capabilities: $networkCapabilities")
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    ) {
                        updateNetworkStatus(connectivityManager)
                    } else {
                        _wifiNetworkAvailable.value = null
                    }
                }

                override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                    Log.e(TAG, "The default network changed link properties: $linkProperties")
                    updateNetworkStatus(connectivityManager)
                }
            })
    }

    private fun updateNetworkStatus(connectivityManager: ConnectivityManager) {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val isWifi = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (isWifi) {
            _wifiNetworkAvailable.value = activeNetwork
        } else {
            _wifiNetworkAvailable.value = null
        }
    }
}
