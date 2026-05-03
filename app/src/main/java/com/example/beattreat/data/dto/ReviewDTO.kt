package com.example.beattreat.data.dto

import com.example.beattreat.ui.Resena.ResenaDetalladaUI
import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")        val id: Int,
    @SerializedName("userId")    val userId: Int,
    @SerializedName("albumId")   val albumId: Int,
    @SerializedName("rating")    val rating: Float,
    @SerializedName("content")   val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("user")      val user: UserInReviewDto
) {
    fun toResenaDetalladaUI(): ResenaDetalladaUI = ResenaDetalladaUI(
        id = this.id.toString(),
        albumId = this.albumId.toString(),
        autorNombre = this.user.name,
        autorUsuario = "@${this.user.username}",
        autorFotoUrl = this.user.profileImage ?: "",
        autorUserId = this.user.id,
        albumNombre = "",
        albumArtista = "",
        albumImagenUrl = "",
        calificacion = this.rating,
        texto = this.content,
        likes = 0,
        comentarios = 0,
        fecha = this.createdAt
    )
}

data class UserInReviewDto(
    @SerializedName("id")           val id: Int,
    @SerializedName("name")         val name: String,
    @SerializedName("username")     val username: String,
    @SerializedName("email")        val email: String,
    @SerializedName("profileImage") val profileImage: String?
)