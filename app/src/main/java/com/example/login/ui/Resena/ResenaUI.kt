package com.example.login.ui.Resena

data class ResenaDetalladaUI(
    val id: Int,
    val albumId: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoUrl: String,
    val albumNombre: String,
    val albumArtista: String,
    val albumImagenUrl: String,
    val calificacion: Float,
    val texto: String,
    val likes: Int,
    val comentarios: Int,
    val fecha: String,
    val autorUserId: Int = 0,
    val autorFirestoreUserId: String = "",
    val autorFotoRes: Int = 0,
    val albumRes: Int = 0,
    val firestoreDocId: String = ""
)

data class ComentarioUI(
    val id: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoUrl: String,
    val texto: String,
    val likes: Int,
    val fecha: String
)