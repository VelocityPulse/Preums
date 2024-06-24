package com.velocitypulse.preums.play

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velocitypulse.preums.core.di.getKoinInstance
import com.velocitypulse.preums.play.network.Host
import com.velocitypulse.preums.play.network.HostClient
import com.velocitypulse.preums.play.network.HostServer
import kotlinx.coroutines.launch

class PlayViewModel : ViewModel() {

    var playState by mutableStateOf<PlayState>(PlayState.HostSelection)
        private set
//    val hostInstances: StateFlow<HostInstance> =

    private var hostInstance: Host? = null

    init {
        Log.i("debugPreums", "init PlayViewModel $this")
    }

    fun onBuzzClick(context: Context) {
        onResume(context)
    }

    fun onStartHostServer(context: Context) {
        playState = PlayState.Hosting
        hostInstance = getKoinInstance<HostServer>().apply {
            viewModelScope.launch { startServer(context) }
        }
    }

    fun onStartDiscovery(context: Context) {
        playState = PlayState.Discovering
        hostInstance = getKoinInstance<HostClient>().apply {
            viewModelScope.launch { startDiscovering() }
//            synchronisedStartDiscovery(context)
        }
    }

    fun onDestroy() {
        hostInstance?.stopProcedures()
    }

    fun onPause() {
        Log.i("debugPreums", "onPause $hostInstance")
        hostInstance?.stopProcedures()
    }

    fun onResume(context: Context) {
        when (playState) {
            is PlayState.Hosting -> {
                viewModelScope.launch { (hostInstance as HostServer).startServer(context) }
            }

            is PlayState.Discovering -> {
                viewModelScope.launch { (hostInstance as HostClient).startDiscovering() }
            }

            else -> {}
        }
    }
}