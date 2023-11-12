package com.velocitypulse.preums.play

sealed class PlayState {
    object HostSelection : PlayState()
    object HostCreation : PlayState()
    object HostJoining : PlayState()
    object Playing : PlayState()
}
