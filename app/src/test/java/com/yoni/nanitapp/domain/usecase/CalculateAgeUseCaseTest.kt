package com.yoni.nanitapp.domain.usecase

import com.yoni.nanitapp.domain.AgeUnit
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate


class CalculateAgeUseCaseTest {

    private val calculateAgeUseCase = CalculateAgeUseCase()

    @Test
    fun `should return 0 months when baby is born today`() {

        val today = LocalDate.now()

        val result = calculateAgeUseCase(today)

        assertEquals(0, result.value)
        assertEquals(AgeUnit.MONTHS, result.unit)
    }

    @Test
    fun `should return 1 year when child is 12 months`() {

        val months = LocalDate.now().minusMonths(12)

        val result = calculateAgeUseCase(months)

        assertEquals(1, result.value)
        assertEquals(AgeUnit.YEARS, result.unit)

    }

    @Test
    fun `should return 1 year when child is 16 months()`() {

        val months = LocalDate.now().minusMonths(16)

        val result = calculateAgeUseCase(months)

        assertEquals(1, result.value)
        assertEquals(AgeUnit.YEARS, result.unit)

    }

    @Test
    fun `should return 1 year when child is 24 months()`() {

        val months = LocalDate.now().minusMonths(24)

        val result = calculateAgeUseCase(months)

        assertEquals(2, result.value)
        assertEquals(AgeUnit.YEARS, result.unit)

    }
}