package core.di

import play.network.HostServer
import com.velocitypulse.preums.play.network.HostClient
import org.koin.core.module.Module
import org.koin.dsl.module


val applicationModule: Module = module {

    single { HostServer() }
    single { HostClient() }

//    single { PlayViewModel(get()) }
//    viewModel { PlayViewModel(get()) }

}