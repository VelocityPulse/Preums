package com.velocitypulse.preums.core.di

import com.velocitypulse.preums.play.PlayViewModel
import com.velocitypulse.preums.play.network.discovery.NetworkDiscoveryClient
import com.velocitypulse.preums.play.network.discovery.NetworkDiscoveryServer
import com.velocitypulse.preums.play.network.core.NetworkInfos
import org.koin.core.module.Module
import org.koin.dsl.module

val applicationModule: Module = module {

    single { NetworkDiscoveryServer(get()) }
    single { NetworkDiscoveryClient(get()) }
    single { NetworkInfos(get()) }

    single { PlayViewModel(get()) }
}
