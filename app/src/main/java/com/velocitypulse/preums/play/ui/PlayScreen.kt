package com.velocitypulse.preums.play.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.core.di.PreviewInitializerProvider
import com.velocitypulse.preums.play.PlayState
import com.velocitypulse.preums.play.PlayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayScreen(navController: NavController, viewModel: PlayViewModel = koinViewModel()) {
    PlayScreen(
        state = viewModel.playState,
        buzzClick = { viewModel.onBuzzClick() }
    )
}

@Composable
fun PlayScreen(state: PlayState, buzzClick: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight(0.5f),
            text = state.javaClass.simpleName
        )
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
//        val hostSelection = state
        HostSelectionDialog()
    }
}

@Composable
@Preview
fun PreviewPlayScreen() {
    PreviewInitializerProvider {
        PlayScreen(navController = rememberNavController())
    }
}