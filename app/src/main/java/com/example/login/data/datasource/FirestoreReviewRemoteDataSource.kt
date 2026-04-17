// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/datasource/FirestoreReviewRemoteDataSource.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.datasource

import com.example.login.data.dto.FirestoreReviewDto

interface FirestoreReviewRemoteDataSource {
    suspend fun getReviewsByAlbum(albumId: String): List<Pair<String, FirestoreReviewDto>>
    suspend fun createReview(dto: FirestoreReviewDto): String
}
