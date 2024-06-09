package play.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import core.di.ApplicationInitializer
import play.Navigation
import org.jetbrains.compose.ui.tooling.preview.Preview

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



