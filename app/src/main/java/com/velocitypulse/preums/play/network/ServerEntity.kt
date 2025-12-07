package com.velocitypulse.preums.play.network

import kotlinx.serialization.Serializable

@Serializable
data class ServerAddress(
    val ip: String,
    val port: Int
)

@Serializable
data class ClientInfo(
    val name: String,
    val ip: String
)

@Serializable
data class ServerInfo(
    val name: String,
    val playersCount: Int,
    val isLocked: Boolean,
    var password: String?,
    val primaryColor: Int,
    val serverAddress: ServerAddress
)
