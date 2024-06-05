package com.velocitypulse.preums.play.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.play.Navigation

class PlayActivity : ComponentActivity() {

    private var applicationInitializer = ApplicationInitializer()

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationInitializer.run(application)

        super.onCreate(savedInstanceState)

        setContent { Navigation() }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Navigation()
    }
}



