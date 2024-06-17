package com.velocitypulse.preums.play

sealed class PlayState {
    object HostSelection : PlayState()
    object Discovering : PlayState()
    object Hosting : PlayState()
    object Playing : PlayState()
}
