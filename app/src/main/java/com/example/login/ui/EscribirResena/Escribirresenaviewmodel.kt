package com.example.login.ui.EscribirResena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.MiPerfilRepository
import com.example.login.ui.AlbumDetalle.AlbumDetalleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EscribirResenaViewModel @Inject constructor(
    private val repository: MiPerfilRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EscribirResenaUIState())
    val uiState: StateFlow<EscribirResenaUIState> = _uiState.asStateFlow()

    fun onTextoChange(texto: String) {
        if (texto.length <= 500) _uiState.update { it.copy(textoResena = texto) }
    }

    fun onCalificacionChange(calificacion: Float) {
        _uiState.update { it.copy(calificacion = calificacion) }
    }

    // Ahora extrae también el id del álbum seleccionado
    fun onAlbumSeleccionado(albumLabel: String) {
        val album = AlbumDetalleData.todos().find {
            "${it.nombre} — ${it.artista}" == albumLabel
        }
        _uiState.update {
            it.copy(
                albumSeleccionado = albumLabel,
                albumId = album?.id ?: 0       // ← guarda el ID real
            )
        }
    }

    fun publicarResena() {
        val state = _uiState.value
        if (state.textoResena.isBlank() || state.calificacion == 0f || state.albumId == 0) return

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