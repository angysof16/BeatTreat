package com.example.login.Comentarios

import androidx.lifecycle.ViewModel
import com.example.login.Resena.ResenaData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ComentariosViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(ComentariosUIState())
    val uiState: StateFlow<ComentariosUIState> = _uiState.asStateFlow()

    fun cargarComentarios(resenaId: Int) {
        // Busca la reseña en todasLasResenas en lugar de resenasDestacadas
        val resena = ResenaData.todasLasResenas.find { it.id == resenaId }
        _uiState.update {
            it.copy(
                resena      = resena,
                comentarios = ResenaData.comentariosEjemplo,
                isLoading   = false
            )
        }
    }

    fun onNuevoComentarioChange(texto: String) {
        _uiState.update { it.copy(nuevoComentario = texto) }
    }

    fun enviarComentario() {
        val texto = _uiState.value.nuevoComentario.trim()
        if (texto.isBlank()) return
        _uiState.update { it.copy(nuevoComentario = "") }
    }

    fun toggleLikeComentario(comentarioId: Int) {
        _uiState.update { state ->
            val likeados = state.comentariosLikeados
            val nuevosLikeados = if (comentarioId in likeados) likeados - comentarioId
            else likeados + comentarioId
            state.copy(comentariosLikeados = nuevosLikeados)
        }
    }
}