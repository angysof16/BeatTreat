package com.example.login.PlaylistDetalle

data class CancionPlaylistUI(
    val titulo: String,
    val artista: String,
    val duracion: String
)

data class PlaylistDetalleUI(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagenRes: Int,
    val canciones: List<CancionPlaylistUI>
)

data class PlaylistDetalleUIState(
    val playlist: PlaylistDetalleUI? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)