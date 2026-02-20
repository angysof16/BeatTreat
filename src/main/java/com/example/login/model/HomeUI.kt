package com.example.login.model

// ── Entidades UI para Home ──

data class BannerUI(
    val id: Int,
    val texto: String,
    val imagenRes: Int
)

data class AlbumHomeUI(
    val id: Int,
    val nombre: String,
    val imagenRes: Int
)

data class ArtistaHomeUI(
    val id: Int,
    val nombre: String,
    val albumes: List<AlbumHomeUI>
)
