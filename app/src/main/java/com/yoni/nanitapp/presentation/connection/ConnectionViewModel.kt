package com.yoni.nanitapp.presentation.connection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()


    fun updateIpAddress(ipAddress: String) {
        _uiState.update { currentState ->
            currentState.copy(
                ipAddress = ipAddress,
                errorMessage = null
            )
        }
    }

    fun updatePort(port: String) {
        _uiState.update { currentState ->
            currentState.copy(
                port = port,
                errorMessage = null
            )
        }
    }

    fun connect() {

    }
}
