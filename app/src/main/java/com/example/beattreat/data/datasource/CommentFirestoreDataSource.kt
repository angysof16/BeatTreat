package com.example.beattreat.data.datasource

import com.example.beattreat.data.dto.FirestoreCommentDto
import kotlinx.coroutines.flow.Flow

interface CommentFirestoreDataSource {
    fun listenCommentsByReview(reviewId: String): Flow<List<Pair<String, FirestoreCommentDto>>>
    suspend fun addComment(dto: FirestoreCommentDto): String
}