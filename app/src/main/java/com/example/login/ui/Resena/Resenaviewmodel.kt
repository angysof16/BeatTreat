// ui/Resena/Resenaviewmodel.kt
package com.example.login.ui.Resena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreReviewLiveRepository
import com.example.login.data.repository.ReviewLikeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResenaViewModel @Inject constructor(
    private val liveReviewRepository: FirestoreReviewLiveRepository,
    private val likeRepository: ReviewLikeRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResenaUIState())
    val uiState: StateFlow<ResenaUIState> = _uiState.asStateFlow()

    private var collectionJob: kotlinx.coroutines.Job? = null

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    fun cargarResenas(albumId: String) {
        _uiState.update { it.copy(isLoading = true, albumId = albumId) }

        collectionJob?.cancel()

        collectionJob = viewModelScope.launch {
            liveReviewRepository.listenReviewsByAlbum(albumId).collect { resenas ->
                // Verificar likes del usuario actual
                val likedIds = mutableSetOf<String>()
                if (currentUserId.isNotBlank()) {
                    for (resena in resenas) {
                        if (resena.firestoreDocId.isNotBlank()) {
                            val isLiked = likeRepository.isLikedBy(resena.firestoreDocId, currentUserId)
                                .getOrNull() ?: false
                            if (isLiked) likedIds.add(resena.firestoreDocId)
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        resenas = resenas,
                        resenasLikeadas = likedIds,
                        isLoading = false,
                        albumNombre = resenas.firstOrNull()?.albumNombre ?: it.albumNombre
                    )
                }
            }
        }
    }

    fun cargarResenas(albumId: Int) {
        cargarResenas(albumId.toString())
    }

    // ui/Resena/Resenaviewmodel.kt
    fun toggleLikeResena(resena: ResenaDetalladaUI) {  // ← Recibir ResenaDetalladaUI
        if (currentUserId.isBlank() || resena.firestoreDocId.isBlank()) return

        val yaLikeado = resena.firestoreDocId in _uiState.value.resenasLikeadas

        viewModelScope.launch {
            // Actualización optimista
            _uiState.update { state ->
                val nuevosLiked = if (yaLikeado)
                    state.resenasLikeadas - resena.firestoreDocId
                else
                    state.resenasLikeadas + resena.firestoreDocId

                val resenasActualizadas = state.resenas.map { r ->
                    if (r.firestoreDocId == resena.firestoreDocId) {
                        r.copy(likes = if (yaLikeado) (r.likes - 1).coerceAtLeast(0)
                        else r.likes + 1)
                    } else r
                }

                state.copy(
                    resenasLikeadas = nuevosLiked,
                    resenas = resenasActualizadas
                )
            }

            // Confirmar con Firestore
            likeRepository.toggleLike(resena.firestoreDocId, currentUserId)
                .onFailure {
                    // Revertir si falló
                    _uiState.update { state ->
                        val revertidos = if (yaLikeado)
                            state.resenasLikeadas + resena.firestoreDocId
                        else
                            state.resenasLikeadas - resena.firestoreDocId

                        val resenasRevertidas = state.resenas.map { r ->
                            if (r.firestoreDocId == resena.firestoreDocId) {
                                r.copy(likes = if (yaLikeado) r.likes + 1
                                else (r.likes - 1).coerceAtLeast(0))
                            } else r
                        }

                        state.copy(
                            resenasLikeadas = revertidos,
                            resenas = resenasRevertidas
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        collectionJob?.cancel()
    }
}