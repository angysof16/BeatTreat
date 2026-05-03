package com.example.beattreat.data.datasource.implementation

import com.example.beattreat.data.datasource.MiReviewRemoteDataSource
import com.example.beattreat.data.dto.CreateReviewDTO
import com.example.beattreat.data.dto.ReviewDto
import com.example.beattreat.data.dto.UpdateReviewDTO
import com.example.beattreat.data.dto.UserReviewDto
import com.example.beattreat.data.network.BeatTreatApiService
import retrofit2.Response
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

    override suspend fun deleteReview(reviewId: Int): Response<Unit> =
        service.deleteReview(reviewId)
}