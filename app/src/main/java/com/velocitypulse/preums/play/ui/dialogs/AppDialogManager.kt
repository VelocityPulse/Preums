package com.velocitypulse.preums.play.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.velocitypulse.preums.play.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.ui.components.WifiWarningDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppDialogManager(viewModel: PlayViewModel = koinViewModel()) {
    val state: PlayState = viewModel.playState

    when (state) {
        PlayState.WiFiWarning -> { WifiWarningDialog() }
        PlayState.StandingForWifi -> StandForWifiDialog()
        PlayState.Discovering -> {}
        PlayState.ServerResearchAndConfigure -> ServerResearchDialog(viewModel)
        PlayState.NetFailure -> NetFailureDialog()
        PlayState.Playing -> {}
        PlayState.MenuSelection -> {}
    }
}
