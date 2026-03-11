package com.example.login.Resena

data class ResenaDetalladaUI(
    val id: Int,
    val albumId: Int, // referencia al álbum al que pertenece la reseña
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoRes: Int,
    val albumNombre: String,
    val albumArtista: String,
    val albumRes: Int,
    val calificacion: Float,
    val texto: String,
    val likes: Int,
    val comentarios: Int,
    val fecha: String
)

data class ComentarioUI(
    val id: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoRes: Int,
    val texto: String,
    val likes: Int,
    val fecha: String
)