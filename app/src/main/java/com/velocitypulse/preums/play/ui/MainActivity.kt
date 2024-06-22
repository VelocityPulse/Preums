package com.velocitypulse.preums.play.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.core.di.PreviewInitializerProvider
import com.velocitypulse.preums.play.Navigation
import com.velocitypulse.preums.play.PlayViewModel
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    private var applicationInitializer = ApplicationInitializer()

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationInitializer.run(application)

        super.onCreate(savedInstanceState)

        setContent { Navigation() }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        PreviewInitializerProvider {
            Navigation()
        }
    }

    override fun onResume() {
        Log.d("debugPreums", "Activity onResume")
        val playViewModel = get<PlayViewModel>()
        playViewModel.onResume(this)
        super.onResume()
    }

    override fun onPause() {
        Log.d("debugPreums", "Activity onPause")
        val playViewModel = get<PlayViewModel>()
        playViewModel.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("debugPreums", "Activity onDestroy")
        val playViewModel = get<PlayViewModel>()

        playViewModel.onDestroy()
        super.onDestroy()
        Log.d("debugPreums", "Activity onDestroy finished")
    }
}



