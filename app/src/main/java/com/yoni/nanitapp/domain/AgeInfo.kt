package com.yoni.nanitapp.domain

data class AgeInfo(
    val value: Int,
    val unit: AgeUnit
)

enum class AgeUnit {
    MONTHS,
    YEARS
}