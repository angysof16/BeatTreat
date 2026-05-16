package com.example.beattreat.data.datasource.implementation.firestore

import com.example.beattreat.data.datasource.CommentFirestoreDataSource
import com.example.beattreat.data.dto.FirestoreCommentDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : CommentFirestoreDataSource {

    companion object {
        private const val COMMENTS_COL = "comments"
    }

    override fun listenCommentsByReview(reviewId: String): Flow<List<Pair<String, FirestoreCommentDto>>> =
        callbackFlow {
            val listener = db.collection(COMMENTS_COL)
                .whereEqualTo("reviewId", reviewId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    if (snapshot != null) {
                        val comments = snapshot.documents.mapNotNull { doc ->
                            val dto = doc.toObject(FirestoreCommentDto::class.java) ?: return@mapNotNull null
                            doc.id to dto
                        }
                        trySend(comments)
                    }
                }
            awaitClose { listener.remove() }
        }

    override suspend fun addComment(dto: FirestoreCommentDto): String {
        val ref = db.collection(COMMENTS_COL).add(dto).await()
        return ref.id
    }
}