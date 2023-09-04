package com.velocitypulse.preums.home.di

import com.velocitypulse.preums.home.ui.HomeActivity
import com.velocitypulse.preums.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module


val homeModule: Module = module {

    scope(named<HomeActivity>()) {
        viewModel { HomeViewModel() }
    }

}