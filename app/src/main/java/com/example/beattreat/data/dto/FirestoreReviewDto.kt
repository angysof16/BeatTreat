package com.example.beattreat.data.dto

data class FirestoreReviewDto(
    val userId: String = "",
    val albumId: String = "",
    val rating: Float = 0f,
    val content: String = "",
    val createdAt: Long = 0L,
    val likesCount: Int = 0,
    val user: FirestoreReviewUserDto = FirestoreReviewUserDto(),

    // coordenadas geográficas donde se publicó el review
    // null = el review fue creado antes del Sprint 4 o el usuario denegó ubicación
    val latitude: Double? = null,
    val longitude: Double? = null
)