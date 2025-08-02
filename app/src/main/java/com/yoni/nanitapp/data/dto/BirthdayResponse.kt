package com.yoni.nanitapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BirthdayResponse(
    val name: String,
    val dob: Long,
    val theme: String
)