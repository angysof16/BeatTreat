package com.example.login.data.datasource.implementation

import com.example.login.data.datasource.MiReviewRemoteDataSource
import com.example.login.data.dto.CreateReviewDTO
import com.example.login.data.dto.ReviewDto
import com.example.login.data.dto.UpdateReviewDTO
import com.example.login.data.dto.UserReviewDto
import com.example.login.data.network.BeatTreatApiService
import javax.inject.Inject

class MiReviewRetrofitDataSource @Inject constructor(
    private val service: BeatTreatApiService
) : MiReviewRemoteDataSource {

    override suspend fun getReviewsByUser(userId: Int): List<UserReviewDto> =
        service.getReviewsByUser(userId)

    override suspend fun createReview(body: CreateReviewDTO): ReviewDto =
        service.createReview(body)

    override suspend fun updateReview(reviewId: Int, body: UpdateReviewDTO): ReviewDto =
        service.updateReview(reviewId, body)

    override suspend fun deleteReview(reviewId: Int) =
        service.deleteReview(reviewId)
}
