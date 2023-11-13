package com.velocitypulse.preums.play

data class HostInstance(
    val name: String,
    val playersCount: Int,
    val isLocked: Boolean,
    val password: String?
)