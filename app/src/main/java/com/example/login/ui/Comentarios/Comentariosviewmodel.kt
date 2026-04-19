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

    /**
     * Carga la reseña buscándola en el backend por albumId, luego filtra por resenaId.
     * Así evitamos necesitar Parcelable: solo navegamos con dos ints.
     *
     * Flujo:
     * 1. GET /albums/:albumId/reviews  → lista de ReviewDto con user incluido
     * 2. Filtramos por resenaId
     * 3. Mostramos comentarios de ejemplo (el backend no tiene endpoint de comentarios aún)
     */
    fun cargarComentarios(resenaId: Int, albumId: Int) {
        _uiState.update { it.copy(isLoading = true) }

        if (albumId != 0) {
            viewModelScope.launch {
                val result = reviewRepository.getReviewsByAlbum(albumId)
                if (result.isSuccess) {
                    val resena = result.getOrDefault(emptyList())
                        .find { it.id == resenaId }
                    _uiState.update {
                        it.copy(
                            resena      = resena ?: buscarEnLocal(resenaId),
                            comentarios = ResenaData.comentariosEjemplo,
                            isLoading   = false
                        )
                    }
                } else {
                    // Si el backend falla, intentamos los datos locales
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
            // Sin albumId: solo datos locales (compatibilidad con ProfileScreen)
            _uiState.update {
                it.copy(
                    resena      = buscarEnLocal(resenaId),
                    comentarios = ResenaData.comentariosEjemplo,
                    isLoading   = false
                )
            }
        }
    }

    /** Busca en los datos hardcodeados locales como fallback */
    private fun buscarEnLocal(resenaId: Int): ResenaDetalladaUI? =
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