package com.velocitypulse.preums.play

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velocitypulse.preums.core.di.getKoinInstance
import com.velocitypulse.preums.play.network.ClientInfo
import com.velocitypulse.preums.play.network.ServerInfo
import com.velocitypulse.preums.play.network.core.NetworkInfos
import com.velocitypulse.preums.play.network.discovery.NetworkBase
import com.velocitypulse.preums.play.network.discovery.NetworkDiscoveryClient
import com.velocitypulse.preums.play.network.discovery.NetworkDiscoveryServer
import com.velocitypulse.preums.play.network.game.GameClient
import com.velocitypulse.preums.play.ui.state.PlayState
import com.velocitypulse.preums.play.ui.state.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

private const val TAG = "PlayViewModel"

class PlayViewModel(private val networkInfos: NetworkInfos) : ViewModel() {

    private val _playState = mutableStateOf<PlayState>(PlayState.WiFiWarning)
    val playState: PlayState by _playState

    private val _uiEvent = MutableStateFlow(UiEvent())
    val uiEvent: StateFlow<UiEvent> get() = _uiEvent

    private val _clientsList = MutableStateFlow<Set<ClientInfo>>(emptySet())
    val clientsList: StateFlow<Set<ClientInfo>> get() = _clientsList

    private val _serverInfoList = MutableStateFlow<Set<ServerInfo>>(emptySet())
    val serverInfoList: StateFlow<Set<ServerInfo>> get() = _serverInfoList

    private var gameClientJob: Job? = null
    private var _gameClient: GameClient? = null

    private var hostInstance: NetworkBase? = null

    fun onWifiWarningDismissed() {
        _playState.value = PlayState.StandingForWifi
        startWifiConnectionSurvey()
    }


    private fun startWifiConnectionSurvey() {
        viewModelScope.launch {
            networkInfos.wifiNetworkAvailable.collect { network ->
                if (network == null && hostInstance == null) {
                    _playState.value = PlayState.StandingForWifi
                } else if (network != null && _playState.value == PlayState.StandingForWifi) {
                    _playState.value = PlayState.MenuSelection
                }
            }
        }
    }

    fun onBuzzClick(context: Context) {
    }

    fun onStartHostServer() {
        _playState.value = PlayState.ServerResearchAndList()
        hostInstance = getKoinInstance<NetworkDiscoveryServer>().apply {
            viewModelScope.launch {
                startServer()

                launch {
                    eventMessage.collect { event ->
                        if (event == NetworkBase.EventState.PORT_FAILURE) {
                            _playState.value = PlayState.NetFailure
                            stopProcedures()
                        }
                    }
                }

                launch {
                    clients.collect { clientsInfo ->
                        Log.i(TAG, "New client: $clientsInfo")
                        when (val state = _playState.value) {
                            is PlayState.ServerResearchAndList -> {
                                _playState.value = state.copy(clients = clientsInfo)
                            }

                            else -> playState
                        }

                    }
                }
            }
        }
    }

    fun onStartHostClient() {
        _playState.value = PlayState.ClientResearchAndList
        hostInstance = getKoinInstance<NetworkDiscoveryClient>().apply {
            viewModelScope.launch(Dispatchers.IO) { startClient() }
            viewModelScope.launch(Dispatchers.IO) {
                discoveredServerInfos.collect { servers ->
                    _serverInfoList.value = servers
                }
            }
        }
    }

    fun onServerSelected(name: String) {
        viewModelScope.launch {
            _serverInfoList.value.find { it.name == name }?.let { serverInfo ->

                serverInfo.password?.let { password ->
                    _uiEvent.emit(UiEvent.ShowServerPasswordQuestion(password))
                } ?: onServerSelected(name, null)
            }
        }
    }

    fun onServerSelected(name: String, password: String?) {
        viewModelScope.launch {
            _serverInfoList.value.find { it.name == name && it.password == password }
                ?.let { serverInfo ->

                    gameClientJob = viewModelScope.launch(Dispatchers.IO) {
                        try {
                            _gameClient = GameClient(serverInfo).apply {
                                // Écouter les événements du jeu
                                onGameEvent { event ->
                                    handleGameEvent(event)
                                }
                            }

                            _gameClient?.connect()
                            _gameState.value = GameState.Connected

                        } catch (e: Exception) {
                            _gameState.value = GameState.Error(e.message ?: "Connection failed")
                            _gameClient = null
                        }
                    }

                }
        }
    }

    fun onCancelResearchDialog() {
        hostInstance?.stopProcedures()
        _playState.value = PlayState.MenuSelection
    }

    fun onDestroy() {
        hostInstance?.stopProcedures()
    }

    fun onPause() {
        Log.i(TAG, "onPause $hostInstance")
        // Not really ?
//        hostInstance?.stopProcedures()
    }

    fun onResume() {
        // TODO : Re-think entirely all this section
        when (_playState.value) {
            is PlayState.ServerResearchAndList -> {
                viewModelScope.launch {
                    (hostInstance as NetworkDiscoveryServer).startServer() // TODO : To recheck
                }
            }

            is PlayState.ClientResearchAndList -> {
                viewModelScope.launch { (hostInstance as NetworkDiscoveryClient).startClient() }
            }

            else -> {}
        }
    }

    @VisibleForTesting
    fun onPreview(state: PlayState) {
        _playState.value = state
    }
}
