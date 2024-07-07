package com.velocitypulse.preums.play.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.core.di.PreviewInitializerProvider
import com.velocitypulse.preums.play.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.ui.PlayScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NetFailureDialog() {
    AlertDialog(modifier = Modifier,
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
                Text(text = "Application was not able to create a server connection.")
            }
        })
}

@Composable
@Preview
fun NetFailureDialogPreview() {
    PreviewInitializerProvider {
        PlayScreen(navController = rememberNavController(),
            viewModel = koinViewModel<PlayViewModel>().apply {
                onPreview(PlayState.NetFailure)
            })
    }
}