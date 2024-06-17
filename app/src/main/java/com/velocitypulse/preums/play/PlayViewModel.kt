package com.velocitypulse.preums.play

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velocitypulse.preums.play.network.HostClient
import com.velocitypulse.preums.play.network.HostServer
import kotlinx.coroutines.launch

class PlayViewModel(private val hostServer: HostServer) : ViewModel() {

    var playState by mutableStateOf<PlayState>(PlayState.HostSelection)
        private set
//    val hostInstances: StateFlow<HostInstance> =


    fun onBuzzClick() {
    }

    fun onStartHostServer(context: Context) {
        playState = PlayState.Hosting
        viewModelScope.launch {
            HostServer().startServer(context)
        }
    }

    fun onStartDiscovery(context: Context) {
        playState = PlayState.Discovering
        viewModelScope.launch {
            HostClient().startDiscovering(context)
        }
    }
}