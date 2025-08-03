package com.yoni.nanitapp.presentation.birthday

import com.yoni.nanitapp.domain.AgeInfo

sealed class BirthdayScreenUiState {
    object Loading : BirthdayScreenUiState()

    data class Error(
        val message: String
    ) : BirthdayScreenUiState()

    data class Success(
        val name: String,
        val ageInfo: AgeInfo,
        val themeResources: ThemeResources,
        val numberIconResource: Int,
        val ageText: String,
        val photoUri: String? = null
    ) : BirthdayScreenUiState()
}