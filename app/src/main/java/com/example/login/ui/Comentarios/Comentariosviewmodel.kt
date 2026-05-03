package com.example.login.ui.Comentarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.ReviewRepository
import com.example.login.ui.Resena.ComentarioUI
import com.example.login.ui.Resena.ResenaData
import com.example.login.ui.Resena.ResenaDetalladaUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComentariosViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComentariosUIState())
    val uiState: StateFlow<ComentariosUIState> = _uiState.asStateFlow()


    fun cargarComentarios(resenaId: String, albumId: String) {
        _uiState.update { it.copy(isLoading = true) }
        val albumIdInt = albumId.toIntOrNull() ?: 0
        if (albumIdInt != 0) {
            viewModelScope.launch {
                val result = reviewRepository.getReviewsByAlbum(albumIdInt)
                if (result.isSuccess) {
                    val resena = result.getOrDefault(emptyList())
                        .find { it.id == resenaId }  // id es String en ResenaDetalladaUI
                    _uiState.update {
                        it.copy(
                            resena      = resena ?: buscarEnLocal(resenaId),
                            comentarios = ResenaData.comentariosEjemplo,
                            isLoading   = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            resena      = buscarEnLocal(resenaId),
                            comentarios = ResenaData.comentariosEjemplo,
                            isLoading   = false
                        )
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    resena      = buscarEnLocal(resenaId),
                    comentarios = ResenaData.comentariosEjemplo,
                    isLoading   = false
                )
            }
        }
    }

    private fun buscarEnLocal(resenaId: String): ResenaDetalladaUI? =
        ResenaData.todasLasResenas.find { it.id == resenaId }

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
            val nuevos = if (comentarioId in likeados) likeados - comentarioId
            else likeados + comentarioId
            state.copy(comentariosLikeados = nuevos)
        }
    }
}