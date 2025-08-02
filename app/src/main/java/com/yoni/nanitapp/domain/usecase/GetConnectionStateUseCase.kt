package com.yoni.nanitapp.domain.usecase

import com.yoni.nanitapp.domain.BirthdayRepository
import com.yoni.nanitapp.domain.ConnectionState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConnectionStateUseCase @Inject constructor(
    private val repository: BirthdayRepository
) {
    operator fun invoke(): Flow<ConnectionState> = repository.getConnectionState()
}