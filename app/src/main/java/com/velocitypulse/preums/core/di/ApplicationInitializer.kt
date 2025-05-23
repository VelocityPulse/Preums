package com.velocitypulse.preums.core.di

import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.velocitypulse.preums.play.theme.PreumsTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class ApplicationInitializer : Application(), KoinComponent {

    companion object {
        lateinit var deviceName: String
            private set

        fun getListOfModules(): List<Module> {
            return listOf(applicationModule)
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationInitializer)
        }

        loadKoinModules(getListOfModules())

        deviceName = getDeviceName(applicationContext)
    }
    private fun getDeviceName(context: Context): String {
        return Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
            ?: Settings.System.getString(context.contentResolver, Settings.System.NAME)
            ?: "Unknown Device"
    }
}

inline fun <reified T> getKoinInstance(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}

@Composable
fun PreviewInitializerProvider(content: @Composable () -> Unit) {
    val context: Context = LocalContext.current
    KoinApplication(application = {
        androidContext(context)
        modules(ApplicationInitializer.getListOfModules())
    }) {
        PreumsTheme { content() }
    }
}