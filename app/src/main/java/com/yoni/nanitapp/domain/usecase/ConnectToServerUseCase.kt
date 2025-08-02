package com.yoni.nanitapp.domain.usecase

import com.yoni.nanitapp.domain.BirthdayData
import com.yoni.nanitapp.domain.BirthdayRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectToServerUseCase @Inject constructor(
    private val repository: BirthdayRepository
) {
    operator fun invoke(ipAddress: String, port: Int): Flow<Result<BirthdayData>> {
        return repository.connectAndListenForMessages(ipAddress, port)
    }
}