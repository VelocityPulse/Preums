package com.velocitypulse.preums.play

import kotlinx.serialization.Serializable

@Serializable
data class HostInstance(
    val ip: String,
    val port: Int,
    var info: HostInfo?
)

@Serializable
data class HostInfo(
    val name: String,
    val playersCount: Int,
    val isLocked: Boolean,
    var password: String?,
    val primaryColor: Int
)