package com.example.login.ui.Home

// ── Entidades UI para Home ──

data class BannerUI(
    val id: Int,
    val texto: String,
    val imagenUrl: String
)

data class AlbumHomeUI(
    val id: Int,
    val nombre: String,
    val imagenUrl: String
)

data class ArtistaHomeUI(
    val id: Int,
    val nombre: String,
    val albumes: List<AlbumHomeUI>
)