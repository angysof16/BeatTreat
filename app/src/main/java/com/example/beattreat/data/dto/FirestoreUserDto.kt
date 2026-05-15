package com.example.beattreat.data.dto

data class FirestoreUserDto(
    val username: String = "",
    val name: String = "",
    val country: String? = null,
    val bio: String? = null,
    val profileImage: String? = null
)