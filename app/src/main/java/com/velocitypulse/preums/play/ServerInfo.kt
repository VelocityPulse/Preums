package com.velocitypulse.preums.play

import kotlinx.serialization.Serializable

@Serializable
data class ServerInfo(
    val ip: String,
    val port: Int,
)

@Serializable
data class ClientInfo(
    val name: String,
)

@Serializable
data class InstanceInfo(
    val name: String,
    val playersCount: Int,
    val isLocked: Boolean,
    var password: String?,
    val primaryColor: Int
)