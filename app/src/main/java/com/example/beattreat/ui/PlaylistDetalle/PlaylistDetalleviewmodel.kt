package com.example.beattreat.ui.PlaylistDetalle

import androidx.lifecycle.ViewModel
import com.example.beattreat.ui.Biblioteca.BibliotecaData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlaylistDetalleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetalleUIState())
    val uiState: StateFlow<PlaylistDetalleUIState> = _uiState.asStateFlow()

    // Canciones de ejemplo para las playlists
    private val cancionesEjemplo = listOf(
        CancionPlaylistUI("Bohemian Rhapsody", "Queen",      "5:55"),
        CancionPlaylistUI("We Will Rock You",  "Queen",      "2:01"),
        CancionPlaylistUI("Radio Ga Ga",       "Queen",      "5:48"),
        CancionPlaylistUI("Don't Stop Me Now", "Queen",      "3:29"),
        CancionPlaylistUI("Another One Bites", "Queen",      "3:36"),
        CancionPlaylistUI("Somebody to Love",  "Queen",      "4:56")
    )

    private val cancionesLoFi = listOf(
        CancionPlaylistUI("Rainy Day",         "Lo-Fi Beats", "3:20"),
        CancionPlaylistUI("Study Session",     "Chillhop",    "4:10"),
        CancionPlaylistUI("Midnight Coffee",   "Lo-Fi Beats", "3:45"),
        CancionPlaylistUI("Cozy Afternoon",    "Chillhop",    "3:58"),
        CancionPlaylistUI("Focus Mode",        "Lo-Fi Beats", "4:22")
    )

    fun cargarPlaylist(playlistId: Int) {
        val playlist = BibliotecaData.playlists.find { it.id == playlistId } ?: return

        val canciones = when (playlistId) {
            1    -> cancionesEjemplo
            2    -> cancionesLoFi
            else -> emptyList()
        }

        _uiState.update {
            it.copy(
                playlist = PlaylistDetalleUI(
                    id            = playlist.id,
                    nombre        = playlist.nombre,
                    descripcion   = playlist.descripcion,
                    coverImageUrl = "",
                    canciones     = canciones
                ),
                isLoading = false
            )
        }
    }
}