package com.example.beattreat.ui.MiPerfil

data class MiResenaUI(
    val id: Int,
    val albumId: Int,
    val albumTitulo: String,
    val albumArtist: String,
    val albumCover: String,
    val rating: Float,
    val content: String,
    val createdAt: String,
    val firestoreDocId: String = ""
)