package com.yoni.nanitapp.data

import com.yoni.nanitapp.data.dto.BirthdayResponse
import com.yoni.nanitapp.domain.BirthdayData
import com.yoni.nanitapp.domain.BirthdayTheme
import java.time.Instant
import java.time.ZoneId

fun BirthdayResponse.toDomain(): BirthdayData = BirthdayData(
    name = this.name,
    theme = BirthdayTheme.fromString(this.theme),
    birthDate = Instant.ofEpochMilli(this.dob)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
)