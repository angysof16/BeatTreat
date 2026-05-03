package com.example.beattreat.data.datasource

import com.example.beattreat.data.dto.CreateReviewDTO
import com.example.beattreat.data.dto.ReviewDto
import com.example.beattreat.data.dto.UpdateReviewDTO
import com.example.beattreat.data.dto.UserReviewDto
import retrofit2.Response

interface MiReviewRemoteDataSource {
    suspend fun getReviewsByUser(userId: Int): List<UserReviewDto>
    suspend fun createReview(body: CreateReviewDTO): ReviewDto
    suspend fun updateReview(reviewId: Int, body: UpdateReviewDTO): ReviewDto
    suspend fun deleteReview(reviewId: Int): Response<Unit>
}