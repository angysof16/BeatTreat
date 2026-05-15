package com.example.beattreat.data.dto

data class RegisterUserDto(
    val username: String = "",
    val name: String = "",
    val country: String? = null,
    val bio: String? = null
)