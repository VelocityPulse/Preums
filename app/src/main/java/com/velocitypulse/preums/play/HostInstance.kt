package com.velocitypulse.preums.play

    data class HostInstance(
    val ip: String,
    val port: Int,
    var info: HostInfo?
)

data class HostInfo(
    val name: String,
    val playersCount: Int,
    val isLocked: Boolean,
    var password: String?,
    val primaryColor: Int
)