package com.yoni.nanitapp.domain.usecase

import com.yoni.nanitapp.domain.BirthdayRepository
import javax.inject.Inject

class DisconnectFromServerUseCase @Inject constructor(
    private val repository: BirthdayRepository
) {
    suspend operator fun invoke() {
        repository.disconnect()
    }
}