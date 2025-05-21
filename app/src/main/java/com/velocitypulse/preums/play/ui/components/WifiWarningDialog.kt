package com.velocitypulse.preums.play.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.velocitypulse.preums.R
import com.velocitypulse.preums.play.PlayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WifiWarningDialog(viewModel: PlayViewModel = koinViewModel()) {
    AlertDialog(
        onDismissRequest = {
            viewModel.onWifiWarningDismissed()
        },
        title = { Text(stringResource(R.string.warning_title)) },
        text = { Text(stringResource(R.string.warning_message)) },
        confirmButton = {
            Button(onClick = {
                viewModel.onWifiWarningDismissed()
            }) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
