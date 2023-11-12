package com.velocitypulse.preums.play.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.theme.PreumsTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostSelectionDialog(viewModel: PlayViewModel = koinViewModel()) {
    AlertDialog(modifier = Modifier,
        title = { Text(text = "Find host") },
        onDismissRequest = { },
        confirmButton = { },
        text = {
            Column {
                Text(text = "qfdsfsd")
            }
        })
//    AlertDialog(
//
//    )
}

@Composable
@Preview
fun HostSelectionDialogPreview() {
    PreumsTheme {
        PlayScreen(navController = rememberNavController())
    }
}