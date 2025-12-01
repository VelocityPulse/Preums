package com.velocitypulse.preums.play

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.velocitypulse.preums.core.di.getKoinInstance
import com.velocitypulse.preums.play.network.Host
import com.velocitypulse.preums.play.network.HostClient
import com.velocitypulse.preums.play.network.HostServer
import com.velocitypulse.preums.play.network.NetworkInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

private const val TAG = "PlayViewModel"

class PlayViewModel(private val networkInfos: NetworkInfos) : ViewModel() {

    var playState by mutableStateOf<PlayState>(PlayState.WiFiWarning)
        private set

    // SERVER
    private val _discoveredHostSharedFlow = MutableSharedFlow<ClientInfo>()
    private val _lostHostSharedFlow = MutableSharedFlow<ClientInfo>()
    private val _eventStateFlow = MutableStateFlow(Host.EventState.NO_EVENT)

    private val _clientsList = MutableStateFlow<Set<ClientInfo>>(emptySet())
    val clientsList: StateFlow<Set<ClientInfo>> get() = _clientsList

    private val _serverList = MutableStateFlow<Set<InstanceInfo>>(emptySet())
    val serverList: StateFlow<Set<InstanceInfo>> get() = _serverList

    private var hostInstance: Host? = null

    fun onWifiWarningDismissed() {
        playState = PlayState.StandingForWifi
        startWifiConnectionSurvey()
    }


    private fun startWifiConnectionSurvey() {
        viewModelScope.launch {
            networkInfos.wifiNetworkAvailable.collect { network ->
                if (network == null && hostInstance == null) {
                    playState = PlayState.StandingForWifi
                } else if (network != null && playState == PlayState.StandingForWifi) {
                    playState = PlayState.MenuSelection
                }
            }
        }
    }

    fun onBuzzClick(context: Context) {
    }

    fun onStartHostServer(context: Context) {
        playState = PlayState.ServerResearchAndList
        hostInstance = getKoinInstance<HostServer>().apply {
            viewModelScope.launch {
                startServer(
                    context,
                    _discoveredHostSharedFlow,
                    _lostHostSharedFlow,
                    _eventStateFlow
                )

                launch {
                    _eventStateFlow.collect { event ->
                        if (event == Host.EventState.PORT_FAILURE) {
                            playState = PlayState.NetFailure
                            stopProcedures()
                        }
                    }
                }

                launch {
                    _discoveredHostSharedFlow.collect { clientInfo ->
                        _clientsList.value += clientInfo
                    }
                }
                launch {
                    _lostHostSharedFlow.collect { clientInfo ->
                        _clientsList.value -= clientInfo
                    }
                }
            }
        }
    }

    fun onStartHostClient(context: Context) {
        playState = PlayState.ClientResearchAndList
        hostInstance = getKoinInstance<HostClient>().apply {
            viewModelScope.launch(Dispatchers.IO) { startDiscovering(context) }
            viewModelScope.launch(Dispatchers.IO) {
                discoveredItems.collect { servers ->
                _serverList.value = servers
            } }
        }
    }

    fun onServerSelected(name: String) {}

    fun onCancelResearchDialog() {
        hostInstance?.stopProcedures()
        playState = PlayState.MenuSelection
    }

    fun onDestroy() {
        hostInstance?.stopProcedures()
    }

    fun onPause() {
        Log.i(TAG, "onPause $hostInstance")
        hostInstance?.stopProcedures()
    }

    fun onResume(context: Context) {
        when (playState) {
            is PlayState.ServerResearchAndList -> {
                viewModelScope.launch {
                    (hostInstance as HostServer).startServer(
                        context,
                        _discoveredHostSharedFlow,
                        _lostHostSharedFlow,
                        _eventStateFlow
                    )
                }
            }

            is PlayState.ClientResearchAndList -> {
                viewModelScope.launch { (hostInstance as HostClient).startDiscovering(context) }
            }

            else -> {}
        }
    }

    @VisibleForTesting
    fun onPreview(state: PlayState) {
        playState = state
    }
}
