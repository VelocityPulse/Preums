package com.velocitypulse.preums.play.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.core.di.PreviewInitializerProvider
import com.velocitypulse.preums.play.ui.state.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.ui.PlayScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun StandForWifiDialog() {
    AlertDialog(
        modifier = Modifier,
        title = { Text(text = "Connection problem") },
        onDismissRequest = { },
        dismissButton = {},
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Waiting Wi-Fi connection...")
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    )
}

@Composable
@Preview
fun StandForWifiDialogPreview() {
    PreviewInitializerProvider {
        PlayScreen(
            navController = rememberNavController(),
            viewModel = koinViewModel<PlayViewModel>().apply {
                onPreview(PlayState.StandingForWifi)
            }
        )
    }
}
