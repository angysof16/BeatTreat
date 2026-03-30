package com.example.login.data.dto

import com.example.login.ui.AlbumDetalle.AlbumDetalleUI
import com.example.login.ui.Home.AlbumHomeUI
import com.google.gson.annotations.SerializedName

// DTO que mapea la respuesta JSON del backend para un álbum.
data class AlbumDto(
    @SerializedName("id")          val id: Int,
    @SerializedName("title")       val title: String,
    @SerializedName("artist")      val artist: String,
    @SerializedName("genre")       val genre: String?,
    @SerializedName("releaseYear") val releaseYear: Int?,
    @SerializedName("coverImage")  val coverImage: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("createdAt")   val createdAt: String?,
    @SerializedName("updatedAt")   val updatedAt: String?
) {
    // Funcion de mapeo DTO
    fun toAlbumDetalleUI(): AlbumDetalleUI = AlbumDetalleUI(
        id                   = this.id,
        nombre               = this.title,
        artista              = this.artist,
        año                  = this.releaseYear?.toString() ?: "—",
        genero               = this.genre ?: "—",
        descripcion          = this.description ?: "",
        imagenRes            = 0,
        duracionTotal        = "—",
        calificacionPromedio = 0f,
        totalResenas         = 0,
        canciones            = emptyList()
    )

    fun toAlbumHomeUI(): AlbumHomeUI = AlbumHomeUI(
        id        = this.id,
        nombre    = this.title,
        imagenRes = 0
    )
}