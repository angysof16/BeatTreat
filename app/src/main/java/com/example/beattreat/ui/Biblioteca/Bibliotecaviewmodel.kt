package com.example.beattreat.ui.Biblioteca

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BibliotecaViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(BibliotecaUIState())
    val uiState: StateFlow<BibliotecaUIState> = _uiState.asStateFlow()

    init {
        cargarBiblioteca()
    }

    private fun cargarBiblioteca() {
        _uiState.update {
            it.copy(
                cancionesGuardadas = BibliotecaData.cancionesGuardadas,
                artistas           = BibliotecaData.artistas,
                albumes            = BibliotecaData.albumes,
                playlists          = BibliotecaData.playlists,
                isLoading          = false
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // Filtra las playlists según el texto de búsqueda
    fun playlistsFiltradas(): List<PlaylistUI> {
        val query = _uiState.value.searchQuery.trim().lowercase()
        return if (query.isEmpty()) {
            _uiState.value.playlists
        } else {
            _uiState.value.playlists.filter { it.nombre.lowercase().contains(query) }
        }
    }
}