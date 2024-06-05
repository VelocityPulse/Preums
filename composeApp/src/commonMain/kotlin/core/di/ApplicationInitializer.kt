package core.di

import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class ApplicationInitializer : KoinComponent {

    fun run() {
        startKoin {
            modules(applicationModule)
        }

        loadKoinModules(
            listOf(
                applicationModule
            )
        )
    }

}