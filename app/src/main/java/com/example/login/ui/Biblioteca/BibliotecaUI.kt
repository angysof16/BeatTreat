package com.example.login.ui.Biblioteca

data class CancionGuardadaUI(
    val id: Int,
    val titulo: String,
    val cantidad: Int,
    val imagenRes: Int
)

data class ArtistaUI(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val imagenRes: Int
)

data class AlbumUI(
    val id: Int,
    val titulo: String,
    val cantidad: Int,
    val imagenRes: Int
)

data class PlaylistUI(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagenRes: Int
)