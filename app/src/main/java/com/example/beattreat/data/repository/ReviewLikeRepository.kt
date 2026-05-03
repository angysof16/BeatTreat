package com.example.beattreat.data.repository

import com.example.beattreat.data.datasource.ReviewLikeRemoteDataSource
import javax.inject.Inject

/**
 * Repositorio de likes en reviews.
 *
 * Sigue el mismo patrón de todas las clases:
 *   - Llama al data source
 *   - Envuelve en Result<T>
 *   - Captura excepciones con mensajes amigables
 *
 * toggleLike retorna Result<Boolean>:
 *   true  = quedó en estado "liked"
 *   false = quedó en estado "unliked"
 */
class ReviewLikeRepository @Inject constructor(
    private val likeDataSource: ReviewLikeRemoteDataSource
) {

    suspend fun toggleLike(reviewId: String, userId: String): Result<Boolean> {
        return try {
            val isLiked = likeDataSource.toggleLike(reviewId, userId)
            Result.success(isLiked)
        } catch (e: Exception) {
            Result.failure(Exception("Error al procesar el like: ${e.message}"))
        }
    }

    suspend fun isLikedBy(reviewId: String, userId: String): Result<Boolean> {
        return try {
            Result.success(likeDataSource.isLikedBy(reviewId, userId))
        } catch (e: Exception) {
            Result.failure(Exception("Error al verificar el like: ${e.message}"))
        }
    }

    suspend fun getLikesCount(reviewId: String): Result<Int> {
        return try {
            Result.success(likeDataSource.getLikesCount(reviewId))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener likes: ${e.message}"))
        }
    }
}
