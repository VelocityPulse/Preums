package com.velocitypulse.preums.core.di

import android.app.Application
import androidx.compose.runtime.Composable
import com.velocitypulse.preums.play.theme.PreumsTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class ApplicationInitializer : KoinComponent {

    companion object {
        fun getListOfModules(): List<Module> {
            return listOf(applicationModule)
        }
    }

    fun run(application: Application) {
        startKoin {
            androidContext(application)
        }

        loadKoinModules(getListOfModules())
    }
}

inline fun <reified T> getKoinInstance(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}

@Composable
fun PreviewInitializerProvider(content: @Composable () -> Unit) {
    KoinApplication(application = {
        modules(ApplicationInitializer.getListOfModules())
    }) {
        PreumsTheme { content() }
    }
}