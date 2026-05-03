package com.example.beattreat.ui.Biblioteca

data class CancionGuardadaUI(
    val id: Int,
    val titulo: String,
    val cantidad: Int,
    val imagenUrl: String
)

data class ArtistaUI(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val imagenUrl: String
)

data class AlbumUI(
    val id: Int,
    val titulo: String,
    val cantidad: Int,
    val imagenUrl: String
)

data class PlaylistUI(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagenUrl: String
)