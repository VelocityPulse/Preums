package com.velocitypulse.preums.play.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.core.di.PreviewInitializerProvider
import com.velocitypulse.preums.play.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ServerResearchAndConfigureDialog(viewModel: PlayViewModel = koinViewModel()) {

//    val clients by viewModel.discoveredHostStateFlow.collectAsState(initial = emptySet())
//    val clients by remember { viewModel.discoveredHostSharedFlow }
    val clients by viewModel.clientsList.collectAsState()


    AlertDialog(modifier = Modifier,
        title = { Text(text = "Find host") },
        onDismissRequest = { },
        dismissButton = {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
//                            dismissed = true
//                        viewModel.onStartHostServer(context)
                    },
            )
        },
        confirmButton = {
            Text(
                text = "Start",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
//                            dismissed = true
//                        viewModel.onStartDiscovery(context)
                    },
            )
        },
        text = {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(clients.toList()) { client ->
                    Text(text = client.toString(), modifier = Modifier)
                }
            }
        })
}

@Composable
@Preview
fun ServerResearchAndConfigureDialogPreview() {
    PreviewInitializerProvider {
        PlayScreen(
            navController = rememberNavController(),
            viewModel = koinViewModel<PlayViewModel>().apply {
                onPreview(PlayState.ServerResearchAndConfigure)
            })
    }
}