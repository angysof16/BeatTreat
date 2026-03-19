package com.example.login.ui.AlbumDetalle

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AlbumDetalleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetalleUIState())
    val uiState: StateFlow<AlbumDetalleUIState> = _uiState.asStateFlow()

    // Carga el álbum por id desde la capa de datos
    fun cargarAlbum(albumId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val album = AlbumDetalleData.findById(albumId)
        if (album != null) {
            _uiState.update { it.copy(album = album, isLoading = false) }
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Álbum no encontrado") }
        }
    }

    fun toggleFavorito() {
        _uiState.update { it.copy(esFavorito = !it.esFavorito) }
    }
}