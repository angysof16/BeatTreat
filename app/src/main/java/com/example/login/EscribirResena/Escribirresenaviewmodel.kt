package com.example.login.EscribirResena

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EscribirResenaViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(EscribirResenaUIState())
    val uiState: StateFlow<EscribirResenaUIState> = _uiState.asStateFlow()

    fun onTextoChange(texto: String) {
        // Limita a 500 caracteres
        if (texto.length <= 500) {
            _uiState.update { it.copy(textoResena = texto) }
        }
    }

    fun onCalificacionChange(calificacion: Float) {
        _uiState.update { it.copy(calificacion = calificacion) }
    }

    fun onAlbumSeleccionado(album: String) {
        _uiState.update { it.copy(albumSeleccionado = album) }
    }

    fun publicarResena() {
        val state = _uiState.value
        if (state.textoResena.isBlank() || state.calificacion == 0f) return
        // En una app real aquí se llamaría al repositorio
        _uiState.update { it.copy(publicadoExitoso = true) }
    }

    fun resetPublicado() {
        _uiState.update { it.copy(publicadoExitoso = false) }
    }
}