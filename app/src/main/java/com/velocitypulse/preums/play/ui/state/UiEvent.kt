package com.velocitypulse.preums.play.ui.state

open class UiEvent {
    
    data class ShowServerPasswordQuestion(val password: String) : UiEvent()

}