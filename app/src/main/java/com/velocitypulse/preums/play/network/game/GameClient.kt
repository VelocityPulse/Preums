package com.velocitypulse.preums.play.network.game

import android.util.Log
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.play.network.ClientInfo
import com.velocitypulse.preums.play.network.ServerInfo
import com.velocitypulse.preums.play.network.core.NetHelper
import com.velocitypulse.preums.play.network.discovery.getLocalIP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val TAG = "GameClient"

class GameClient(private val netHelper: NetHelper) {



}