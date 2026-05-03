package com.example.beattreat.ui.MiPerfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.data.repository.FirestoreReviewRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MiPerfilViewModel @Inject constructor(
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val firestoreAlbumRepository: FirestoreAlbumRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(MiPerfilUIState())
    val uiState: StateFlow<MiPerfilUIState> = _uiState.asStateFlow()

    init {
        cargarMisResenas()
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    fun cargarMisResenas() {
        val userId = firebaseAuth.currentUser?.uid ?: run {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Load reviews and album metadata in parallel
            val reviewsDeferred = async { firestoreReviewRepository.getReviewsByUserRaw(userId) }
            val albumsDeferred  = async { firestoreAlbumRepository.getAllAlbumsRaw() }

            val reviewsResult = reviewsDeferred.await()
            val albumsMap     = albumsDeferred.await().getOrDefault(emptyMap())

            reviewsResult.onSuccess { pairs ->
                val lista = pairs.map { (docId, dto) ->
                    val albumDto = albumsMap[dto.albumId]
                    MiResenaUI(
                        id             = docId.hashCode(),
                        albumId        = dto.albumId.hashCode(),
                        albumTitulo    = albumDto?.title  ?: "Álbum desconocido",
                        albumArtist    = albumDto?.artist ?: "",
                        albumCover     = albumDto?.coverImage ?: "",
                        rating         = dto.rating,
                        content        = dto.content,
                        createdAt      = dto.createdAt.toString(),
                        firestoreDocId = docId
                    )
                }
                _uiState.update { it.copy(misResenas = lista, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────

    fun abrirFormularioEditar(resena: MiResenaUI) {
        _uiState.update {
            it.copy(
                mostrarFormulario = true,
                resenaEnEdicion   = resena,
                formularioAlbumId = resena.albumId,
                formularioRating  = resena.rating,
                formularioContent = resena.content
            )
        }
    }

    fun cerrarFormulario() {
        _uiState.update { it.copy(mostrarFormulario = false, resenaEnEdicion = null) }
    }

    fun onRatingChange(rating: Float) {
        _uiState.update { it.copy(formularioRating = rating) }
    }

    fun onContentChange(content: String) {
        _uiState.update { it.copy(formularioContent = content) }
    }

    /** Only called for EDIT (create navigates to EscribirResenaScreen). */
    fun guardarResena() {
        val state = _uiState.value
        val resena  = state.resenaEnEdicion ?: return
        val content = state.formularioContent.trim()
        if (content.isBlank() || state.formularioRating == 0f) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            firestoreReviewRepository.updateReview(
                reviewDocId = resena.firestoreDocId,
                rating      = state.formularioRating,
                content     = content
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        mostrarFormulario = false,
                        resenaEnEdicion   = null,
                        isLoading         = false,
                        successMessage    = "¡Reseña actualizada!"
                    )
                }
                cargarMisResenas()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    fun pedirConfirmarEliminar(resena: MiResenaUI) {
        _uiState.update { it.copy(mostrarConfirmarEliminar = true, resenaAEliminar = resena) }
    }

    fun cancelarEliminar() {
        _uiState.update { it.copy(mostrarConfirmarEliminar = false, resenaAEliminar = null) }
    }

    fun confirmarEliminar() {
        val resena = _uiState.value.resenaAEliminar ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarConfirmarEliminar = false) }

            firestoreReviewRepository.deleteReview(resena.firestoreDocId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading       = false,
                            resenaAEliminar = null,
                            successMessage  = "Reseña eliminada"
                        )
                    }
                    cargarMisResenas()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    // ── Messages ──────────────────────────────────────────────────────────────

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}