package com.yoni.nanitapp.data.repository

import com.yoni.nanitapp.data.datasource.WebSocketDataSource
import com.yoni.nanitapp.data.datasource.WebSocketEvent
import com.yoni.nanitapp.data.toDomain
import com.yoni.nanitapp.domain.BirthdayData
import com.yoni.nanitapp.domain.BirthdayRepository
import com.yoni.nanitapp.domain.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BirthdayRepositoryImpl @Inject constructor(
    private val webSocketDataSource: WebSocketDataSource
) : BirthdayRepository {

    private val _cachedBirthdayData = MutableStateFlow<BirthdayData?>(null)
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)

    override fun connectAndListenForMessages(
        ipAddress: String,
        port: Int
    ): Flow<Result<BirthdayData>> = webSocketDataSource.connectAndListenForEvents(ipAddress, port)
        .onEach { event ->
            updateConnectionState(event)
        }
        .filterIsInstance<WebSocketEvent.MessageReceived>()
        .map { event ->
            try {
                val birthdayData = event.response.toDomain()
                _cachedBirthdayData.value = birthdayData
                Result.success(birthdayData)
            } catch (e: Exception) {
                Result.failure(
                    IllegalArgumentException(
                        "Failed to parse birthday data: ${e.message}",
                        e
                    )
                )
            }
        }

    private fun updateConnectionState(event: WebSocketEvent) {
        _connectionState.value = when (event) {
            is WebSocketEvent.Connecting -> ConnectionState.Connecting
            is WebSocketEvent.Connected -> ConnectionState.Connected
            is WebSocketEvent.Disconnected -> ConnectionState.Idle
            is WebSocketEvent.Error -> ConnectionState.Error(event.message)
            is WebSocketEvent.MessageReceived -> ConnectionState.MessageReceived
        }
    }

    override suspend fun disconnect() {
        webSocketDataSource.disconnect()
        _cachedBirthdayData.value = null
        _connectionState.value = ConnectionState.Idle
    }

    override fun observeCachedBirthdayData(): Flow<BirthdayData?> =
        _cachedBirthdayData.asStateFlow()

    override fun getConnectionState(): Flow<ConnectionState> = _connectionState.asStateFlow()
}