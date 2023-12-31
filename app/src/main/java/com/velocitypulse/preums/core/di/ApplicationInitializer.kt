package com.velocitypulse.preums.core.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ApplicationInitializer : KoinComponent {

    fun run(application: Application) {
        startKoin {
            androidContext(application)
        }

        loadKoinModules(
            listOf(
                applicationModule
            )
        )
    }

}