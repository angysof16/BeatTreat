package com.example.login.AlbumDetalle

data class AlbumDetalleUIState(
    val album: AlbumDetalleUI? = null,
    val esFavorito: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)