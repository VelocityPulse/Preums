package com.velocitypulse.preums.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PlayViewModel : ViewModel() {

    val playState by mutableStateOf<PlayState>(PlayState.HostSelection)

    fun onBuzzClick() {
    }

}