package com.example.beattreat.ui.Biblioteca

data class BibliotecaUIState(
    val searchQuery: String = "",
    val cancionesGuardadas: CancionGuardadaUI? = null,
    val artistas: ArtistaUI? = null,
    val albumes: AlbumUI? = null,
    val playlists: List<PlaylistUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)