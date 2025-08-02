package com.yoni.nanitapp.domain

import kotlinx.coroutines.flow.Flow

interface BirthdayRepository {
    fun connectAndListenForMessages(ipAddress: String, port: Int): Flow<Result<BirthdayData>>
    fun getConnectionState(): Flow<ConnectionState>
    suspend fun disconnect()
    fun observeCachedBirthdayData(): Flow<BirthdayData?>
}