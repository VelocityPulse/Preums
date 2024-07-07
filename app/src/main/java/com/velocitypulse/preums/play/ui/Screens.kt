package com.velocitypulse.preums.play.ui

sealed class Screens(val route: String) {
    object GraphScreen : Screens("graph_screen")
    object PlayScreen : Screens("play_screen")
}
