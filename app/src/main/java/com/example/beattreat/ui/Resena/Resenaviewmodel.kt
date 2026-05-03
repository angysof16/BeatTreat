// ui/Resena/Resenaviewmodel.kt
package com.example.beattreat.ui.Resena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.data.repository.FirestoreReviewLiveRepository
import com.example.beattreat.data.repository.ReviewLikeRepository
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
    private val firebaseAuth: FirebaseAuth,
    // AÑADIR esta dependencia:
    private val firestoreAlbumRepository: FirestoreAlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResenaUIState())
    val uiState: StateFlow<ResenaUIState> = _uiState.asStateFlow()
    private var collectionJob: kotlinx.coroutines.Job? = null
    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    // Esta función ya existía para String — la dejamos igual
    fun cargarResenas(albumId: String) {
        // Si el albumId es un número (hashCode del firestoreId), lo resolvemos primero
        val albumIdAsInt = albumId.toIntOrNull()
        if (albumIdAsInt != null) {
            cargarResenas(albumIdAsInt)
            return
        }
        // Ya es un firestoreId real (string)
        _uiState.update { it.copy(isLoading = true, albumId = albumId) }
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch {
            liveReviewRepository.listenReviewsByAlbum(albumId).collect { resenas ->

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
                        resenas         = resenas,
                        resenasLikeadas = likedIds,
                        isLoading       = false,
                        albumNombre     = resenas.firstOrNull()?.albumNombre ?: it.albumNombre
                    )
                }
            }
        }
    }

    // REEMPLAZAR la versión Int — ya no asume que el hashCode == firestoreId
    fun cargarResenas(albumId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Buscar el firestoreId real a partir del hashCode
            val rawResult = firestoreAlbumRepository.getAllAlbumsRaw()
            val albumsMap = rawResult.getOrDefault(emptyMap())
            val entry     = albumsMap.entries.find { it.key.hashCode() == albumId }

            if (entry != null) {
                // Tenemos el firestoreId real, ya podemos escuchar reviews
                cargarResenas(entry.key)
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Álbum no encontrado")
                }
            }
        }
    }

    // El resto de funciones quedan igual (toggleLikeResena, onCleared)
    fun toggleLikeResena(resena: ResenaDetalladaUI) {
        if (currentUserId.isBlank() || resena.firestoreDocId.isBlank()) return
        val yaLikeado = resena.firestoreDocId in _uiState.value.resenasLikeadas
        viewModelScope.launch {
            _uiState.update { state ->
                val nuevosLiked = if (yaLikeado) state.resenasLikeadas - resena.firestoreDocId
                else state.resenasLikeadas + resena.firestoreDocId
                val resenasActualizadas = state.resenas.map { r ->
                    if (r.firestoreDocId == resena.firestoreDocId)
                        r.copy(likes = if (yaLikeado) (r.likes - 1).coerceAtLeast(0) else r.likes + 1)
                    else r
                }
                state.copy(resenasLikeadas = nuevosLiked, resenas = resenasActualizadas)
            }
            likeRepository.toggleLike(resena.firestoreDocId, currentUserId).onFailure {
                _uiState.update { state ->
                    val revertidos = if (yaLikeado) state.resenasLikeadas + resena.firestoreDocId
                    else state.resenasLikeadas - resena.firestoreDocId
                    val resenasRevertidas = state.resenas.map { r ->
                        if (r.firestoreDocId == resena.firestoreDocId)
                            r.copy(likes = if (yaLikeado) r.likes + 1 else (r.likes - 1).coerceAtLeast(0))
                        else r
                    }
                    state.copy(resenasLikeadas = revertidos, resenas = resenasRevertidas)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        collectionJob?.cancel()
    }
}