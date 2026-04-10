package com.example.login.ui.EscribirResena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AlbumRepository
import com.example.login.data.repository.MiPerfilRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EscribirResenaViewModel @Inject constructor(
    private val repository: MiPerfilRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EscribirResenaUIState())
    val uiState: StateFlow<EscribirResenaUIState> = _uiState.asStateFlow()

    init {
        cargarAlbumesDelBackend()
    }

    /**
     * Carga los álbumes reales del backend para poder obtener sus IDs correctos.
     */
    private fun cargarAlbumesDelBackend() {
        viewModelScope.launch {
            _uiState.update { it.copy(albumesCargando = true) }
            try {
                // AlbumRepository devuelve Result<List<ArtistaHomeUI>> agrupado,
                // así que accedemos directamente al datasource a través del repositorio.
                // Usamos el resultado de getAllAlbums que ya tenemos disponible,
                // pero necesitamos los DTOs crudos. Lo hacemos con una llamada directa:
                val result = albumRepository.getAllAlbumsDto()
                result.onSuccess { dtos ->
                    _uiState.update { it.copy(albumesBackend = dtos, albumesCargando = false) }
                }.onFailure {
                    // Si falla, dejamos lista vacía; el selector mostrará los locales como fallback
                    _uiState.update { it.copy(albumesCargando = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(albumesCargando = false) }
            }
        }
    }

    fun onTextoChange(texto: String) {
        if (texto.length <= 500) _uiState.update { it.copy(textoResena = texto) }
    }

    fun onCalificacionChange(calificacion: Float) {
        _uiState.update { it.copy(calificacion = calificacion) }
    }

    /**
     * Busca el álbum seleccionado primero en la lista del backend (IDs reales),
     * y como fallback en los datos locales.
     */
    fun onAlbumSeleccionado(albumLabel: String) {
        val state = _uiState.value

        // 1. Intentar encontrar en la lista del backend (formato "título — artista")
        val dtoEncontrado = state.albumesBackend.find { dto ->
            "${dto.title} — ${dto.artist}" == albumLabel
        }

        if (dtoEncontrado != null) {
            _uiState.update {
                it.copy(
                    albumSeleccionado = albumLabel,
                    albumId           = dtoEncontrado.id
                )
            }
            return
        }

        // 2. Fallback: buscar por título parcial si el formato no coincide exacto
        val dtoParcial = state.albumesBackend.find { dto ->
            albumLabel.contains(dto.title, ignoreCase = true) &&
                    albumLabel.contains(dto.artist, ignoreCase = true)
        }

        if (dtoParcial != null) {
            _uiState.update {
                it.copy(
                    albumSeleccionado = albumLabel,
                    albumId           = dtoParcial.id
                )
            }
            return
        }

        // 3. Si no hay backend disponible, quedamos sin albumId válido
        _uiState.update {
            it.copy(
                albumSeleccionado = albumLabel,
                albumId           = 0,
                errorMessage      = "No se pudo identificar el álbum. Verifica la conexión."
            )
        }
    }

    fun publicarResena() {
        val state = _uiState.value
        if (state.textoResena.isBlank() || state.calificacion == 0f || state.albumId == 0) {
            _uiState.update {
                it.copy(errorMessage = "Selecciona un álbum válido, calificación y escribe tu reseña.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.crearResena(
                albumId = state.albumId,
                rating  = state.calificacion,
                content = state.textoResena
            )
            if (result.isSuccess) {
                _uiState.update { it.copy(publicadoExitoso = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun resetPublicado() {
        _uiState.update { it.copy(publicadoExitoso = false) }
    }
}