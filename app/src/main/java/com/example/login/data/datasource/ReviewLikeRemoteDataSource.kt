package com.example.login.data.datasource

/**
 * Contrato para las operaciones de Like en reviews.
 *
 * Usa subcolecciones igual que el profesor:
 *   reviews/{reviewId}/likes/{userId}
 *
 * toggleLike:  agrega el doc si no existe (like), lo elimina si ya existe (dislike)
 *              y actualiza el contador likesCount en el documento padre mediante transacción.
 * isLikedBy:   retorna true si el usuario ya le dio like a esa review.
 * getLikesCount: retorna el contador actual de likes de una review.
 */
interface ReviewLikeRemoteDataSource {
    suspend fun toggleLike(reviewId: String, userId: String): Boolean  // true = liked, false = unliked
    suspend fun isLikedBy(reviewId: String, userId: String): Boolean
    suspend fun getLikesCount(reviewId: String): Int
}
