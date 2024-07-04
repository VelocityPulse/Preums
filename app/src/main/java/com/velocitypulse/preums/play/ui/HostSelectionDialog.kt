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
import com.velocitypulse.preums.core.di.PreviewInitializerProvider
import com.velocitypulse.preums.play.PlayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostSelectionDialog(viewModel: PlayViewModel = koinViewModel()) {
    val context = LocalContext.current

//    if (!dismissed) {
    AlertDialog(modifier = Modifier,
        title = { Text(text = "Find host") },
        onDismissRequest = { },
        dismissButton = {
            Text(
                text = "Create party",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
//                            dismissed = true
                        viewModel.onStartHostServer(context)
                    },
            )
        },
        confirmButton = {
            Text(
                text = "Join party",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
//                            dismissed = true
                        viewModel.onStartDiscovery(context)
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

@Composable
@Preview
fun HostSelectionDialogPreview() {
    PreviewInitializerProvider {
        PlayScreen(navController = rememberNavController())
    }
}