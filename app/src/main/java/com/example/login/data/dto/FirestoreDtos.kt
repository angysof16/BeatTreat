// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/dto/FirestoreUserDto.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.dto

data class FirestoreUserDto(
    val username: String = "",
    val name: String = "",
    val country: String? = null,
    val bio: String? = null,
    val profileImage: String? = null
)

// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/dto/RegisterUserDto.kt
// ──────────────────────────────────────────────────────────────────────────────
data class RegisterUserDto(
    val username: String = "",
    val name: String = "",
    val country: String? = null,
    val bio: String? = null
)

data class FirestoreAlbumDto(
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val releaseYear: Int = 0,
    val coverImage: String = "",
    val description: String = ""
)

data class FirestoreReviewDto(
    val userId: String = "",
    val albumId: String = "",
    val rating: Float = 0f,
    val content: String = "",
    val createdAt: Long = 0L,
    val likesCount: Int = 0,
    val user: FirestoreReviewUserDto = FirestoreReviewUserDto()
)

data class FirestoreReviewUserDto(
    val name: String = "",
    val username: String = "",
    val profileImage: String? = null
)
