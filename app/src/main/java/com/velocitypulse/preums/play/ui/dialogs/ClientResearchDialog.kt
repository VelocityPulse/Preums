package com.velocitypulse.preums.play.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.core.di.PreviewInitializerProvider
import com.velocitypulse.preums.play.ui.state.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.ui.PlayScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ClientResearchDialog(
    viewModel: PlayViewModel = koinViewModel()
) {
    val servers by viewModel.serverInfoList.collectAsState()

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text("Available Servers", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                if (servers.isEmpty()) {
                    Text(
                        "No servers found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        items(servers.toList()) { server ->
                            ServerItem(
                                name = server.name,
                                playersCount = server.playersCount,
                                isLocked = server.isLocked,
                                color = Color(server.primaryColor),
                                onClick = { viewModel.onServerSelected(server.name) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Text(
                "Close",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { /* Close dialog */ }
            )
        }
    )
}

@Composable
private fun ServerItem(
    name: String,
    playersCount: Int,
    isLocked: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "$playersCount players",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
@Preview
fun ClientResearchDialogPreview() {
    PreviewInitializerProvider {
        PlayScreen(
            navController = rememberNavController(),
            viewModel = koinViewModel<PlayViewModel>().apply {
                onPreview(PlayState.ClientResearchAndList)
            }
        )
    }
}
