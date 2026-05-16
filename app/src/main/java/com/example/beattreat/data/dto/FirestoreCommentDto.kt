package com.example.beattreat.data.dto

data class FirestoreCommentDto(
    val reviewId: String = "",
    val userId: String = "",
    val content: String = "",
    val createdAt: Long = 0L,
    val user: FirestoreReviewUserDto = FirestoreReviewUserDto()
)