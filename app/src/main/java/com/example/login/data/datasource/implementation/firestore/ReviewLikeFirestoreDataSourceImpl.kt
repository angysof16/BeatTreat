package com.example.login.data.datasource.implementation.firestore

import com.example.login.data.datasource.ReviewLikeRemoteDataSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementación Firestore de likes en reviews.
 *
 * Estructura en Firestore:
 *   reviews/
 *     {reviewId}/
 *       likesCount: Int          ← contador desnormalizado para UI rápida
 *       likes/                   ← subcolección
 *         {userId}/
 *           timestamp: Long
 *
 * Usa TRANSACCIÓN exactamente como el profesor explicó:
 *   1. Verifica si el doc del userId ya existe en la subcolección
 *   2. Si existe  → elimina el like y decrementa likesCount en -1
 *   3. Si no existe → crea el like y  incrementa likesCount en +1
 *
 * Así garantizamos atomicidad: si falla cualquier paso,
 * Firebase hace rollback automático.
 */
class ReviewLikeFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReviewLikeRemoteDataSource {

    companion object {
        private const val REVIEWS_COL = "reviews"
        private const val LIKES_COL   = "likes"
        private const val LIKES_COUNT = "likesCount"
    }

    /**
     * Alterna like/dislike de una review.
     * Retorna true si quedó en estado "liked", false si quedó "unliked".
     */
    override suspend fun toggleLike(reviewId: String, userId: String): Boolean {
        val reviewRef = db.collection(REVIEWS_COL).document(reviewId)
        val likeRef   = reviewRef.collection(LIKES_COL).document(userId)

        return db.runTransaction { transaction ->
            val likeDoc = transaction.get(likeRef)

            if (likeDoc.exists()) {
                // Ya tenía like → quitar like (dislike)
                transaction.delete(likeRef)
                transaction.update(reviewRef, LIKES_COUNT, FieldValue.increment(-1))
                false  // quedó "unliked"
            } else {
                // No tenía like → dar like
                transaction.set(likeRef, mapOf("timestamp" to System.currentTimeMillis()))
                transaction.update(reviewRef, LIKES_COUNT, FieldValue.increment(1))
                true   // quedó "liked"
            }
        }.await()
    }

    /**
     * Verifica si el usuario ya le dio like a esta review.
     * Se usa al cargar el detalle para pintar el corazón correctamente.
     */
    override suspend fun isLikedBy(reviewId: String, userId: String): Boolean {
        val likeDoc = db.collection(REVIEWS_COL)
            .document(reviewId)
            .collection(LIKES_COL)
            .document(userId)
            .get()
            .await()
        return likeDoc.exists()
    }

    /**
     * Obtiene el contador de likes desde el campo desnormalizado.
     * Más eficiente que contar documentos de la subcolección.
     */
    override suspend fun getLikesCount(reviewId: String): Int {
        val doc = db.collection(REVIEWS_COL).document(reviewId).get().await()
        return (doc.getLong(LIKES_COUNT) ?: 0L).toInt()
    }
}
