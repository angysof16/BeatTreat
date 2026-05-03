package com.example.beattreat.data.dto

/**
 * Documento que se guarda en la subcolección:
 *   reviews/{reviewId}/likes/{userId}
 *
 * El ID del documento ES el userId, igual que hizo el profesor con tweets.
 * Solo guardamos el timestamp para saber cuándo ocurrió el like.
 */
data class LikeDto(
    val timestamp: Long = System.currentTimeMillis()
)
