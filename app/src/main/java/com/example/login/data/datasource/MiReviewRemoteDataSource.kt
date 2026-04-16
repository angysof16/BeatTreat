package com.example.login.data.datasource

import com.example.login.data.dto.CreateReviewDTO
import com.example.login.data.dto.ReviewDto
import com.example.login.data.dto.UpdateReviewDTO
import com.example.login.data.dto.UserReviewDto
import retrofit2.Response

interface MiReviewRemoteDataSource {
    suspend fun getReviewsByUser(userId: Int): List<UserReviewDto>
    suspend fun createReview(body: CreateReviewDTO): ReviewDto
    suspend fun updateReview(reviewId: Int, body: UpdateReviewDTO): ReviewDto
    suspend fun deleteReview(reviewId: Int): Response<Unit>
}