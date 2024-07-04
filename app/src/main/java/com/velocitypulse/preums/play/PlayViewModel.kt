package com.velocitypulse.preums.play

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velocitypulse.preums.core.di.getKoinInstance
import com.velocitypulse.preums.play.network.Host
import com.velocitypulse.preums.play.network.HostClient
import com.velocitypulse.preums.play.network.HostServer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

private const val TAG = "PlayViewModel"

class PlayViewModel : ViewModel() {

    var playState by mutableStateOf<PlayState>(PlayState.MenuSelection)
        private set

    // SERVER
    private val _discoveredHostSharedFlow = MutableSharedFlow<ClientInfo>()
    private val _lostHostSharedFlow = MutableSharedFlow<ClientInfo>()

    private val _clientsList = MutableStateFlow<Set<ClientInfo>>(emptySet())
    val clientsList: StateFlow<Set<ClientInfo>> get() = _clientsList

    private var hostInstance: Host? = null

    init {
        Log.i(TAG, "init PlayViewModel $this")
    }

    fun onBuzzClick(context: Context) {
        onResume(context)
    }

    fun onStartHostServer(context: Context) {
        playState = PlayState.ServerResearchAndConfigure
        hostInstance = getKoinInstance<HostServer>().apply {
            viewModelScope.launch {
                startServer(context, _discoveredHostSharedFlow, _lostHostSharedFlow)

                viewModelScope.launch {
                    _discoveredHostSharedFlow.collect { clientInfo ->
                        _clientsList.value += clientInfo
                    }
                }
                viewModelScope.launch {
                    _lostHostSharedFlow.collect { clientInfo ->
                        _clientsList.value -= clientInfo
                    }
                }
            }
        }
    }

    fun onStartDiscovery(context: Context) {
        playState = PlayState.Discovering
        hostInstance = getKoinInstance<HostClient>().apply {
            viewModelScope.launch { startDiscovering() }
//            synchronisedStartDiscovery(context)
        }
    }

    fun onCancelReasearchDialog() {
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
            is PlayState.ServerResearchAndConfigure -> {
                viewModelScope.launch {
                    (hostInstance as HostServer).startServer(
                        context,
                        _discoveredHostSharedFlow,
                        _lostHostSharedFlow
                    )
                }
            }

            is PlayState.Discovering -> {
                viewModelScope.launch { (hostInstance as HostClient).startDiscovering() }
            }

            else -> {}
        }
    }

    @VisibleForTesting
    fun onPreview(state: PlayState) {
        playState = state
    }
}