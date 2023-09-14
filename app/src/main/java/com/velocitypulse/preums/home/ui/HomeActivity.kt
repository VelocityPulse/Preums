package com.velocitypulse.preums.home.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.home.theme.PreumsTheme

class HomeActivity : ComponentActivity() {

    private var applicationInitializer = ApplicationInitializer()

    override fun onCreate(savedInstanceState: Bundle?) {

        applicationInitializer.run(application)

        super.onCreate(savedInstanceState)

        setContent { MainView() }
    }

    @Composable
    fun MainView() {
        PreumsTheme {
//            ExerciseGraph(Modifier.background(MaterialTheme.colorScheme.inverseOnSurface))
//            ColorThemePrinter()
//            BackgroundColorPrinter(Modvendu ifier . background (MaterialTheme.colorScheme.inverseOnSurface))
            PushableButton()
        }
    }

    @Composable
    fun PushableButton(modifier: Modifier = Modifier) {
        Box(Modifier.fillMaxSize()) {
            Button(
                modifier = modifier
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {

                },
            ) {}
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() { //        ExerciseGraph()
        MainView()
    }
}



