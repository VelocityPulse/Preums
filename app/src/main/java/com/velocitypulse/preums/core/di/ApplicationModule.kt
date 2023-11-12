package com.velocitypulse.preums.core.di

import com.velocitypulse.preums.play.PlayViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


val applicationModule: Module = module {

    viewModel { PlayViewModel() }

}