package com.example.login.GeneroDetalle

data class AlbumGeneroUI(val id: Int, val nombre: String, val artista: String)

data class GeneroDetalleUIState(
    val nombre: String = "",
    val colorFondo: Long = 0xFF6366F1,
    val albumes: List<AlbumGeneroUI> = emptyList(),
    val isLoading: Boolean = false
)