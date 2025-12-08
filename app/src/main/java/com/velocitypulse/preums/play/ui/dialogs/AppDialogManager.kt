package com.velocitypulse.preums.play.ui.dialogs

import androidx.compose.runtime.Composable
import com.velocitypulse.preums.play.ui.state.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppDialogManager(viewModel: PlayViewModel = koinViewModel()) {
    val state: PlayState = viewModel.playState

    when (state) {
        is PlayState.WiFiWarning -> WifiWarningDialog()
        is PlayState.StandingForWifi -> StandForWifiDialog()
        is PlayState.ClientResearchAndList -> ClientResearchDialog()
        is PlayState.ServerResearchAndList -> ServerResearchDialog(viewModel)
        is PlayState.NetFailure -> NetFailureDialog()
        is PlayState.Playing -> {}
        is PlayState.MenuSelection -> {}
    }
}
