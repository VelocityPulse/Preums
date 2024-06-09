package play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velocitypulse.preums.play.theme.PreumsTheme
import com.velocitypulse.preums.exercise.ui.GraphScreen
import com.velocitypulse.preums.play.ui.Screens
import play.ui.PlayScreen

@Composable
fun Navigation() {
    PreumsTheme {
        // Nav controller is not that useful in this application as we only have one screen
        // with a lot of states
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.PlayScreen.route) {
            composable(route = Screens.GraphScreen.route) {
                GraphScreen(navController = navController)
            }
            composable(route = Screens.PlayScreen.route) {
                PlayScreen(navController = navController)
            }
        }
    }

}

// Typicall usage of navigation for when we want to change page
@Composable
fun NavigationPreview(navController: NavController) {
    Box(Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(CircleShape)
                .size(200.dp)
                .background(MaterialTheme.colorScheme.primary),
            onClick = {
                navController.navigate(Screens.GraphScreen.route)
            },
        ) {}
    }
}
