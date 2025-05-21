package com.velocitypulse.preums.play.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.velocitypulse.preums.play.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.ui.components.WifiWarningDialog
import com.velocitypulse.preums.play.ui.dialogs.HostSelectionDialog
import com.velocitypulse.preums.play.ui.dialogs.NetFailureDialog
import com.velocitypulse.preums.play.ui.dialogs.ServerResearchDialog
import com.velocitypulse.preums.play.ui.dialogs.StandForWifiDialog
import kotlinx.coroutines.flow.StateFlow
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
