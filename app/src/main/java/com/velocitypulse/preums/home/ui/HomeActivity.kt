package com.velocitypulse.preums.home.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.home.theme.PreumsTheme

class HomeActivity : ComponentActivity() {

    private var applicationInitializer = ApplicationInitializer()

    override fun onCreate(savedInstanceState: Bundle?) {

        applicationInitializer.run(application)

        super.onCreate(savedInstanceState)

        setContent {
            MainView()
        }
    }

    @Composable
    fun MainView() {
        PreumsTheme { // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top
                ) {
                    Greeting("Android")
                    PushableButton(MaterialTheme.colorScheme.inversePrimary)
                }
            }
        }
    }

    @Composable
    fun PushableButton(color: Color, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier) {
            drawCircle(
                color = color,
                center = center,
                radius = 200.dp / 2f.dp
            )
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!", modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MainView()
    }
}



