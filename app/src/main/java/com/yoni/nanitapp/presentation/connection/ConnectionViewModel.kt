package com.yoni.nanitapp.presentation.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoni.nanitapp.domain.ConnectionState
import com.yoni.nanitapp.domain.usecase.ConnectToServerUseCase
import com.yoni.nanitapp.domain.usecase.DisconnectFromServerUseCase
import com.yoni.nanitapp.domain.usecase.GetConnectionStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val connectToServerUseCase: ConnectToServerUseCase,
    private val disconnectFromServerUseCase: DisconnectFromServerUseCase,
    private val getConnectionStateUseCase: GetConnectionStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    private var connectionJob: Job? = null

    fun updateIpAddress(ipAddress: String) {
        _uiState.value = _uiState.value.copy(
            ipAddress = ipAddress,
            errorMessage = null
        )
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
        val currentState = _uiState.value
        // TODO: Extract validation logic into InputValidator class
        if (currentState.ipAddress.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter an IP address")
            return
        }

        if (currentState.port.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter a port number")
            return
        }

        val port = currentState.port.toIntOrNull()
        if (port == null || port <= 0 || port > 65535) {
            return
        }
        _uiState.update { currentState ->
            currentState.copy(
                connectionStatus = ConnectionStatus.CONNECTING,
                errorMessage = null,
                connectionStatusMessage = "Connecting to ${currentState.ipAddress}:${currentState.port}..."
            )
        }

        connectionJob?.cancel()

        connectionJob = viewModelScope.launch {
            try {
                launch { observeConnectionState() }
                launch { observeMessages(currentState.ipAddress, port) }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        connectionStatus = ConnectionStatus.IDLE,
                        errorMessage = "Connection failed: ${e.message}",
                        connectionStatusMessage = "Connection failed"
                    )
                }
            }
        }
    }

    private suspend fun observeConnectionState() {
        getConnectionStateUseCase()
            .collect { state ->
                _uiState.update { currentState ->
                    currentState.copy(
                        connectionStatus = when (state) {
                            is ConnectionState.Connecting -> ConnectionStatus.CONNECTING
                            is ConnectionState.Connected, is ConnectionState.MessageReceived -> ConnectionStatus.CONNECTED
                            is ConnectionState.Idle, is ConnectionState.Error -> ConnectionStatus.IDLE
                        },
                        connectionStatusMessage = when (state) {
                            is ConnectionState.Connecting -> "Connecting..."
                            is ConnectionState.Connected -> "Connected! Waiting for birthday data"
                            is ConnectionState.MessageReceived -> "Connected! Waiting for birthday data"
                            is ConnectionState.Idle -> "Disconnected"
                            is ConnectionState.Error -> "Connection error: ${state.message}"
                        },
                        errorMessage = if (state is ConnectionState.Error) state.message else null
                    )
                }
            }
    }

    private suspend fun observeMessages(ipAddress: String, port: Int) {
        connectToServerUseCase(ipAddress, port)
            .collect { result ->
                result.fold(
                    onSuccess = { message ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                receivedBirthdayData = true,
                                connectionStatusMessage = "Birthday data received"
                            )
                        }
                        _navigationEvent.emit(NavigationEvent.NavigateToBirthday)
                    },
                    onFailure = { error ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                errorMessage = "Message error: ${error.message}",
                                connectionStatusMessage = "Error receiving data"
                            )

                        }
                    }
                )
            }
    }

    fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null

        viewModelScope.launch {
            disconnectFromServerUseCase()
            _uiState.update { currentState ->
                currentState.copy(
                    connectionStatus = ConnectionStatus.IDLE,
                    receivedBirthdayData = false,
                    connectionStatusMessage = "Disconnected",
                    errorMessage = null
                )
            }
        }
    }
}
