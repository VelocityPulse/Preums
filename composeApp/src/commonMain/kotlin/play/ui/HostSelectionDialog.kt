package play.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.theme.PreumsTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun HostSelectionDialog(viewModel: PlayViewModel = koinInject()) {
//    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val dismissed by viewModel.dismissed

    if (!dismissed) {
        AlertDialog(modifier = Modifier,
            title = { Text(text = "Find host") },
            onDismissRequest = { },
            dismissButton = {
                Text(
                    text = "host server",
//                    style = MaterialTheme.typography.labelLarge,
//                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            viewModel.dismissDialog()
                            scope.launch {
//                                HostServer().startServer(context)
                            }
                        },
                )
            },
            confirmButton = {
                Text(
                    text = "discover",
//                    style = MaterialTheme.typography.labelLarge,
//                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            viewModel.dismissDialog()
                            scope.launch {
//                                HostClient().startClient(context)
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