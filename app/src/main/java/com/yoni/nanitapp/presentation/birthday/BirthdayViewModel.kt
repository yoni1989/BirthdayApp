package com.yoni.nanitapp.presentation.birthday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoni.nanitapp.domain.usecase.CalculateAgeUseCase
import com.yoni.nanitapp.domain.usecase.ObserveCachedBirthdayDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirthdayViewModel @Inject constructor(
    private val observeCachedBirthdayDataUseCase: ObserveCachedBirthdayDataUseCase,
    private val calculateAgeUseCase: CalculateAgeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BirthdayScreenUiState>(BirthdayScreenUiState.Loading)
    val uiState: StateFlow<BirthdayScreenUiState> = _uiState.asStateFlow()

    init {
        observeCachedBirthdayData()
    }

    private fun observeCachedBirthdayData() {
        viewModelScope.launch {
            observeCachedBirthdayDataUseCase()
                .collect { cachedBirthdayData ->
                    if (cachedBirthdayData != null) {
                        try {
                            val ageInfo = calculateAgeUseCase(cachedBirthdayData.birthDate)
                            val themeResources = cachedBirthdayData.theme.toThemeResources()
                            val numberIconResource = ageInfo.value.toNumberIconResource()
                            val ageText = ageInfo.toDisplayText()

                            val currentPhotoUri = when (val currentState = _uiState.value) {
                                is BirthdayScreenUiState.Success -> currentState.photoUri
                                else -> null
                            }

                            _uiState.value = BirthdayScreenUiState.Success(
                                name = cachedBirthdayData.name,
                                ageInfo = ageInfo,
                                themeResources = themeResources,
                                numberIconResource = numberIconResource,
                                ageText = ageText,
                                photoUri = currentPhotoUri
                            )
                        } catch (e: Exception) {
                            _uiState.value = BirthdayScreenUiState.Error(
                                message = "Error processing birthday data: ${e.message}"
                            )
                        }
                    } else {
                        _uiState.value = BirthdayScreenUiState.Error(
                            message = "No birthday data available. Please connect to server first."
                        )
                    }
                }
        }
    }

    fun updatePhotoUri(uri: String) {
        _uiState.update { currentState ->
            when (currentState) {
                is BirthdayScreenUiState.Success -> currentState.copy(photoUri = uri)
                else -> currentState
            }
        }
    }
}