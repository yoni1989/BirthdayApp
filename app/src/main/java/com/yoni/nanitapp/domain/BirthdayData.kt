package com.yoni.nanitapp.domain

import java.time.LocalDate

data class BirthdayData(
    val name: String,
    val birthDate: LocalDate,
    val theme: BirthdayTheme
)

enum class BirthdayTheme {
    PELICAN,
    FOX,
    ELEPHANT;

    companion object {
        fun fromString(theme: String): BirthdayTheme = when (theme.lowercase()) {
            "pelican" -> PELICAN
            "fox" -> FOX
            "elephant" -> ELEPHANT
            else -> FOX
        }
    }
}