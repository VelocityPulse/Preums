package com.velocitypulse.preums.play

sealed class PlayState {
    data object MenuSelection : PlayState()
    data object Discovering : PlayState()
    data object ServerResearchAndConfigure : PlayState()
    data object Playing : PlayState()
}
