package com.yoni.nanitapp.domain.usecase

import com.yoni.nanitapp.domain.AgeInfo
import com.yoni.nanitapp.domain.AgeUnit
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class CalculateAgeUseCase @Inject constructor() {

    operator fun invoke(birthDate: LocalDate): AgeInfo {
        return try {
            val today = LocalDate.now()

            if (birthDate.isAfter(today)) {
                return AgeInfo(0, AgeUnit.MONTHS)
            }

            val period = Period.between(birthDate, today)
            val totalMonths = period.toTotalMonths().toInt()

            when {
                totalMonths < 0 -> AgeInfo(0, AgeUnit.MONTHS)
                totalMonths < 12 -> AgeInfo(totalMonths, AgeUnit.MONTHS)
                else -> {
                    val years = totalMonths / 12
                    AgeInfo(years, AgeUnit.YEARS)
                }
            }
        } catch (e: Exception) {
            // Fallback for any date calculation errors
            AgeInfo(0, AgeUnit.MONTHS)
        }
    }
}