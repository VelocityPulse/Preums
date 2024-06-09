package play.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.play.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.theme.PreumsTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun PlayScreen(navController: NavController, viewModel: PlayViewModel = koinInject()) {
    PlayScreen(
        state = viewModel.playState,
        buzzClick = { viewModel.onBuzzClick() }
    )
}

@Composable
fun PlayScreen(state: PlayState, buzzClick: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(CircleShape)
                .size(200.dp)
                .background(MaterialTheme.colorScheme.primary),
            onClick = buzzClick,
        ) {}
    }

    if (state is PlayState.HostSelection) {
        val hostSelection = state as PlayState.HostSelection
        HostSelectionDialog()
    }
}

@Composable
@Preview
fun PreviewPlayScreen() {
    PreumsTheme {
        PlayScreen(navController = rememberNavController())
    }
}