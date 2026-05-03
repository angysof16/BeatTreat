package com.example.beattreat.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para los reviews que devuelve GET /users/:userId/reviews.
 * El backend incluye el objeto "album" anidado (join con Sequelize).
 */
data class UserReviewDto(
    @SerializedName("id")        val id: Int,
    @SerializedName("userId")    val userId: Int,
    @SerializedName("albumId")   val albumId: Int,
    @SerializedName("rating")    val rating: Float,
    @SerializedName("content")   val content: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("album")     val album: AlbumInReviewDto?
)

/**
 * Sub-DTO con los datos del álbum incluidos en cada review.
 */
data class AlbumInReviewDto(
    @SerializedName("id")         val id: Int,
    @SerializedName("title")      val title: String,
    @SerializedName("artist")     val artist: String,
    @SerializedName("coverImage") val coverImage: String?
)
