package com.example.login.data.datasource.implementation.firestore

import com.example.login.data.datasource.FirestoreReviewRemoteDataSource
import com.example.login.data.dto.FirestoreReviewDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirestoreReviewRemoteDataSource {

    companion object {
        private const val REVIEWS_COLLECTION = "reviews"
    }

    override suspend fun getReviewsByAlbum(albumId: String): List<Pair<String, FirestoreReviewDto>> {
        val snapshot = db.collection(REVIEWS_COLLECTION)
            .whereEqualTo("albumId", albumId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val review = doc.toObject(FirestoreReviewDto::class.java) ?: return@mapNotNull null
            doc.id to review
        }
    }

    override suspend fun createReview(dto: FirestoreReviewDto): String {
        val ref = db.collection(REVIEWS_COLLECTION).add(dto).await()
        return ref.id
    }

    // ← NUEVO: filtra por userId para cargar reviews de un usuario específico
    override suspend fun getReviewsByUser(userId: String): List<Pair<String, FirestoreReviewDto>> {
        val snapshot = db.collection(REVIEWS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val review = doc.toObject(FirestoreReviewDto::class.java) ?: return@mapNotNull null
            doc.id to review
        }
    }
}