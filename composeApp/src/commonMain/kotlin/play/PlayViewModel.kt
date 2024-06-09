package com.velocitypulse.preums.play

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import play.network.HostServer

class PlayViewModel(private val hostServer: HostServer) : ViewModel() {

    private val _dismissed = mutableStateOf(false)
    val dismissed: State<Boolean> get() = _dismissed

    fun dismissDialog() {
        _dismissed.value = true
    }

    fun showDialog() {
        _dismissed.value = false
    }

    val playState by mutableStateOf<PlayState>(PlayState.HostSelection)
//    val hostInstances: StateFlow<HostInstance> =

    fun onBuzzClick() {
    }

}