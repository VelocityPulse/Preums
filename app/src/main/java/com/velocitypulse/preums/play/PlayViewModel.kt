package com.velocitypulse.preums.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.velocitypulse.preums.play.network.HostServer

class PlayViewModel(private val hostServer: HostServer) : ViewModel() {

    val playState by mutableStateOf<PlayState>(PlayState.HostSelection)

//    val hostInstances: StateFlow<HostInstance> =

    fun onBuzzClick() {
    }

}