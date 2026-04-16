package com.example.login.ui.ArtistaDetalle

data class AlbumArtistaUI(val id: Int, val nombre: String, val imagenUrl: String)

data class ArtistaDetalleUI(
    val id: Int,
    val nombre: String,
    val albumes: List<AlbumArtistaUI>
)

data class ArtistaDetalleUIState(
    val artista: ArtistaDetalleUI? = null,
    val siguiendo: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)