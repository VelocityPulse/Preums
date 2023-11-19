package com.velocitypulse.preums.play.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.network.HostClient
import com.velocitypulse.preums.play.network.HostServer
import com.velocitypulse.preums.play.theme.PreumsTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostSelectionDialog(viewModel: PlayViewModel = koinViewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    var dismissed by rememberSaveable {
        mutableStateOf(false)
    }

    if (!dismissed) {
        AlertDialog(modifier = Modifier,
            title = { Text(text = "Find host") },
            onDismissRequest = { },
            dismissButton = {
                Text(
                    text = "host server",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            dismissed = true
                            scope.launch {
                                HostServer().startServer(context)
                            }
                        },
                )
            },
            confirmButton = {
                Text(
                    text = "discover",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            dismissed = true
                            scope.launch {
                                HostClient().startClient(context)
                            }
                        },
                )
            },
            text = {
/*
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items() {

                    }
                }
*/
            })
    }
}

@Composable
@Preview
fun HostSelectionDialogPreview() {
    PreumsTheme {
        PlayScreen(navController = rememberNavController())
    }
}