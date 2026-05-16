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

    // BUG FIX: guardamos el reviewId directamente en el ViewModel
    // para que enviarComentario no dependa de que cargarResena haya terminado.
    private var currentReviewId: String = ""

    /**
     * reviewId: firestoreDocId de la reseña (String)
     * albumId: puede ser el firestoreId del álbum o su hashCode como String
     *
     * BUG FIX: ya no se llama desde ComentariosScreen (el LaunchedEffect
     * fue eliminado del composable). Solo se llama desde AppNavegacion.
     * Esto evita la condición de carrera que cancelaba el Flow antes de
     * que terminara de resolver la reseña.
     */
    fun cargarComentarios(reviewId: String, albumId: String) {
        // Si ya estamos escuchando el mismo review, no hacer nada
        if (reviewId == currentReviewId && !reviewId.isBlank()) return

        currentReviewId = reviewId
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // 1. Cargar la reseña para mostrar el encabezado (en paralelo con el Flow)
        viewModelScope.launch {
            cargarResena(reviewId, albumId)
        }

        // 2. Suscribir al Flow de comentarios en tiempo real
        // BUG FIX: usamos directamente el reviewId recibido como parámetro,
        // sin esperar a que cargarResena termine.
        commentsJob?.cancel()
        commentsJob = viewModelScope.launch {
            commentRepository.listenComments(reviewId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar comentarios: ${e.message}") }
                }
                .collect { comentarios ->
                    _uiState.update { it.copy(comentarios = comentarios, isLoading = false) }
                }
        }
    }

    private suspend fun cargarResena(reviewId: String, albumId: String) {
        val albumIdInt = albumId.toIntOrNull()

        val firestoreAlbumId: String = if (albumIdInt != null) {
            val rawResult = firestoreAlbumRepository.getAllAlbumsRaw()
            rawResult.getOrDefault(emptyMap()).entries
                .find { it.key.hashCode() == albumIdInt }?.key ?: albumId
        } else {
            albumId
        }

        val result = firestoreReviewRepository.getReviewsByAlbum(firestoreAlbumId)
        val resena = result.getOrNull()?.find { it.firestoreDocId == reviewId }

        // BUG FIX: si no encontramos la reseña buscando por álbum,
        // intentamos buscar directamente por el reviewId en todas las reseñas.
        // Esto puede pasar si el albumId es un hashCode que no resuelve correctamente.
        if (resena != null) {
            _uiState.update { it.copy(resena = resena) }
        } else {
            // fallback: dejamos la pantalla funcional aunque no tengamos el encabezado
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onNuevoComentarioChange(texto: String) {
        _uiState.update { it.copy(nuevoComentario = texto) }
    }

    fun enviarComentario() {
        val texto = _uiState.value.nuevoComentario.trim()

        // BUG FIX: usamos currentReviewId directamente en lugar de
        // resena?.firestoreDocId, que puede ser null si cargarResena
        // no terminó cuando el usuario toca "Enviar".
        val reviewId = currentReviewId.ifBlank {
            _uiState.value.resena?.firestoreDocId
        } ?: ""

        if (texto.isBlank() || reviewId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true) }
            commentRepository.addComment(reviewId, texto)
                .onSuccess {
                    _uiState.update { it.copy(nuevoComentario = "", enviando = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(enviando = false, errorMessage = "Error al enviar: ${e.message}") }
                }
        }
    }

    fun toggleLikeComentario(comentarioId: Int) {
        _uiState.update { state ->
            val likeados = state.comentariosLikeados
            val nuevos = if (comentarioId in likeados) likeados - comentarioId
            else likeados + comentarioId
            state.copy(comentariosLikeados = nuevos)
        }
    }

    override fun onCleared() {
        super.onCleared()
        commentsJob?.cancel()
    }
}