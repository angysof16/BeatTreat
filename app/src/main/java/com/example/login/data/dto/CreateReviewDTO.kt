package com.example.login.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para CREAR una reseña (POST /reviews)
 * Se envía en el body del request.
 */
data class CreateReviewDTO(
    @SerializedName("userId")  val userId: Int,
    @SerializedName("albumId") val albumId: Int,
    @SerializedName("rating")  val rating: Float,
    @SerializedName("content") val content: String
)

/**
 * DTO para EDITAR una reseña (PUT /reviews/:id)
 * Solo rating y content son modificables.
 */
data class UpdateReviewDTO(
    @SerializedName("rating")  val rating: Float,
    @SerializedName("content") val content: String
)
