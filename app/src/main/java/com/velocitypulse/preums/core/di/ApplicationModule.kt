package com.velocitypulse.preums.core.di

import com.velocitypulse.preums.play.network.HostServer
import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.network.HostClient
import com.velocitypulse.preums.play.ui.MainActivity
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


val applicationModule: Module = module {

    single { HostServer() }
    single { HostClient() }

    single { PlayViewModel() }

}