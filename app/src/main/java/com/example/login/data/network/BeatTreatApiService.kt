package com.example.login.data.network

import com.example.login.data.dto.AlbumDto
import com.example.login.data.dto.CreateReviewDTO
import com.example.login.data.dto.ReviewDto
import com.example.login.data.dto.UpdateReviewDTO
import com.example.login.data.dto.UserDto
import com.example.login.data.dto.UserReviewDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BeatTreatApiService {

    @GET("albums")
    suspend fun getAllAlbums(): List<AlbumDto>

    @GET("albums/{id}")
    suspend fun getAlbumById(@Path("id") id: Int): AlbumDto

    @GET("albums/{albumId}/reviews")
    suspend fun getReviewsByAlbum(@Path("albumId") albumId: Int): List<ReviewDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto

    @GET("users/{userId}/reviews")
    suspend fun getReviewsByUser(@Path("userId") userId: Int): List<UserReviewDto>

    // ── CRUD Reviews ─────────────────────────────────────────────────────────

    /** POST /reviews */
    @POST("reviews")
    suspend fun createReview(@Body body: CreateReviewDTO): ReviewDto

    /** PUT /reviews/:id */
    @PUT("reviews/{id}")
    suspend fun updateReview(
        @Path("id") reviewId: Int,
        @Body body: UpdateReviewDTO
    ): ReviewDto

    /** DELETE /reviews/:id */
    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") reviewId: Int)
}
