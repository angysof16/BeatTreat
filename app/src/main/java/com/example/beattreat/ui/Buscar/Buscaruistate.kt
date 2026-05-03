package com.example.beattreat.ui.Buscar

data class BuscarUIState(
    val query: String = "",
    val resultadosAlbumes: List<AlbumBuscarUI> = emptyList(),
    val resultadosArtistas: List<ArtistaBuscarUI> = emptyList(),
    val isLoading: Boolean = false
)