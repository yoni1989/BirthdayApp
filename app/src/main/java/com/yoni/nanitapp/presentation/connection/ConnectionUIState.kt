package com.yoni.nanitapp.presentation.connection

enum class ConnectionStatus {
    IDLE,
    CONNECTING,
    CONNECTED
}

data class ConnectionUiState(
    val ipAddress: String = "10.100.102.7",
    val port: String = "8080",
    val connectionStatus: ConnectionStatus = ConnectionStatus.IDLE,
    val errorMessage: String? = null,
    val receivedBirthdayData: Boolean = false,
    val connectionStatusMessage: String = "Ready to connect"
)

sealed class NavigationEvent {
    object NavigateToBirthday : NavigationEvent()
}