package com.velocitypulse.preums.exercise.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.NavHostController
import com.velocitypulse.preums.play.ui.Screens

@Composable
fun GraphScreen(navController: NavHostController) {
    RandomGraphCompose()
    Button(
        modifier = Modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent
        ),
        shape = RectangleShape,
        onClick = {
            navController.navigate(Screens.PlayScreen.route)
        }
    ) {}
}
