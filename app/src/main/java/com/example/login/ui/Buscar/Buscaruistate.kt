package com.example.login.ui.Buscar

data class BuscarUIState(
    val query: String = "",
    val resultadosAlbumes: List<AlbumBuscarUI> = emptyList(),
    val resultadosArtistas: List<ArtistaBuscarUI> = emptyList(),
    val isLoading: Boolean = false
)