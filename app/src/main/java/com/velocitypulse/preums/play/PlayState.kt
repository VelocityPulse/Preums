package com.velocitypulse.preums.play

sealed class PlayState {
    data object WiFiWarning : PlayState()
    data object StandingForWifi : PlayState()
    data object MenuSelection : PlayState()

    data object ClientResearchAndList : PlayState()

    data object ServerResearchAndList : PlayState()
    data object NetFailure : PlayState()
    data object Playing : PlayState()
}
