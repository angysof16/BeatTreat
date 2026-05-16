package com.example.beattreat.ui.Comentarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.CommentRepository
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.data.repository.FirestoreReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComentariosViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val firestoreAlbumRepository: FirestoreAlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComentariosUIState())
    val uiState: StateFlow<ComentariosUIState> = _uiState.asStateFlow()

    private var commentsJob: Job? = null

    /**
     * reviewId: firestoreDocId de la reseña (String)
     * albumId: puede ser el firestoreId del álbum o su hashCode como String
     */
    fun cargarComentarios(reviewId: String, albumId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // 1. Cargar la reseña para mostrar el encabezado
        viewModelScope.launch {
            cargarResena(reviewId, albumId)
        }

        // 2. Suscribir al Flow de comentarios en tiempo real
        commentsJob?.cancel()
        commentsJob = viewModelScope.launch {
            commentRepository.listenComments(reviewId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { comentarios ->
                    _uiState.update { it.copy(comentarios = comentarios, isLoading = false) }
                }
        }
    }

    private suspend fun cargarResena(reviewId: String, albumId: String) {
        // Intentar buscar en Firestore usando el firestoreDocId
        // albumId puede ser un Int hashCode o un String firestoreId
        val albumIdInt = albumId.toIntOrNull()

        val firestoreAlbumId: String = if (albumIdInt != null) {
            // Resolver hashCode → firestoreId real
            val rawResult = firestoreAlbumRepository.getAllAlbumsRaw()
            rawResult.getOrDefault(emptyMap()).entries
                .find { it.key.hashCode() == albumIdInt }?.key ?: albumId
        } else {
            albumId
        }

        val result = firestoreReviewRepository.getReviewsByAlbum(firestoreAlbumId)
        val resena = result.getOrNull()?.find { it.firestoreDocId == reviewId }

        _uiState.update { it.copy(resena = resena) }
    }

    fun onNuevoComentarioChange(texto: String) {
        _uiState.update { it.copy(nuevoComentario = texto) }
    }

    fun enviarComentario() {
        val texto    = _uiState.value.nuevoComentario.trim()
        val reviewId = _uiState.value.resena?.firestoreDocId ?: return
        if (texto.isBlank() || reviewId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true) }
            commentRepository.addComment(reviewId, texto)
                .onSuccess {
                    _uiState.update { it.copy(nuevoComentario = "", enviando = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(enviando = false, errorMessage = e.message) }
                }
        }
    }

    // Los likes de comentarios siguen siendo locales (UI only) por ahora
    fun toggleLikeComentario(comentarioId: Int) {
        _uiState.update { state ->
            val likeados = state.comentariosLikeados
            val nuevos   = if (comentarioId in likeados) likeados - comentarioId
            else likeados + comentarioId
            state.copy(comentariosLikeados = nuevos)
        }
    }

    override fun onCleared() {
        super.onCleared()
        commentsJob?.cancel()
    }
}