package com.example.login.ArtistaDetalle

import androidx.lifecycle.ViewModel
import com.example.login.Home.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ArtistaDetalleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistaDetalleUIState())
    val uiState: StateFlow<ArtistaDetalleUIState> = _uiState.asStateFlow()

    fun cargarArtista(artistaId: Int) {
        val artista = HomeData.artistas.find { it.id == artistaId }
        if (artista != null) {
            _uiState.update {
                it.copy(
                    artista = ArtistaDetalleUI(
                        id      = artista.id,
                        nombre  = artista.nombre,
                        albumes = artista.albumes.map { a ->
                            AlbumArtistaUI(id = a.id, nombre = a.nombre, imagenRes = a.imagenRes)
                        }
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun toggleSeguir() {
        _uiState.update { it.copy(siguiendo = !it.siguiendo) }
    }
}