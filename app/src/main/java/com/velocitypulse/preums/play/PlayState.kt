package com.velocitypulse.preums.play

import com.velocitypulse.preums.play.network.ClientInfo

sealed class PlayState {
    data object WiFiWarning : PlayState()
    data object StandingForWifi : PlayState()
    data object MenuSelection : PlayState()

    data object ClientResearchAndList : PlayState()

    data class ServerResearchAndList(
        val clients: Set<ClientInfo> = emptySet()
    ) : PlayState()
    data object NetFailure : PlayState()
    data object Playing : PlayState()
}
