package com.example.login.ui.Resena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreAlbumRepository
import com.example.login.data.repository.FirestoreReviewRepository
import com.example.login.data.repository.ReviewLikeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FIX Sprint 3 — Bug #1 (likes)
 *
 * Problema original: toggleLikeResena solo actualizaba el Set local
 * likedReviewIds pero NO llamaba a Firestore ni actualizaba el contador
 * numérico visible en la UI.
 *
 * Solución:
 *  1. Se inyectan ReviewLikeRepository y FirebaseAuth.
 *  2. Al cargar las reseñas, se verifica cuáles ya tienen like del usuario.
 *  3. toggleLikeResena llama a Firestore y actualiza el contador en la lista.
 */
@HiltViewModel
class ResenaViewModel @Inject constructor(
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val firestoreAlbumRepository: FirestoreAlbumRepository,
    private val likeRepository: ReviewLikeRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResenaUIState())
    val uiState: StateFlow<ResenaUIState> = _uiState.asStateFlow()

    private var currentFirestoreAlbumId: String = ""

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    fun cargarResenas(firestoreAlbumId: String) {
        currentFirestoreAlbumId = firestoreAlbumId
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = firestoreReviewRepository.getReviewsByAlbum(firestoreAlbumId)
            if (result.isSuccess) {
                val resenas = result.getOrDefault(emptyList())
                // FIX: verificar qué reviews ya tienen like del usuario actual
                val likedIds = if (currentUserId.isNotBlank()) {
                    resenas.mapNotNull { resena ->
                        if (resena.firestoreDocId.isNotBlank()) {
                            val liked = likeRepository.isLikedBy(resena.firestoreDocId, currentUserId)
                                .getOrNull() ?: false
                            if (liked) resena.id else null
                        } else null
                    }.toSet()
                } else emptySet()

                _uiState.update {
                    it.copy(
                        resenas          = resenas,
                        resenasLikeadas  = likedIds,
                        isLoading        = false
                    )
                }
            } else {
                val resenasLocales = ResenaData.porAlbum(firestoreAlbumId.hashCode())
                _uiState.update {
                    it.copy(
                        resenas      = resenasLocales,
                        isLoading    = false,
                        errorMessage = if (resenasLocales.isEmpty())
                            result.exceptionOrNull()?.message else null
                    )
                }
            }
        }
    }

    fun cargarResenas(albumId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val rawResult = firestoreAlbumRepository.getAllAlbumsRaw()
            val firestoreId = rawResult.getOrNull()
                ?.keys?.find { it.hashCode() == albumId }
                ?: albumId.toString()

            currentFirestoreAlbumId = firestoreId
            cargarResenas(firestoreId)
        }
    }

    fun getCurrentFirestoreAlbumId(): String = currentFirestoreAlbumId

    /**
     * FIX: ahora llama a Firestore y actualiza el número visible de likes.
     *
     * Flujo:
     *  1. Actualización optimista del Set de IDs likeados (corazón rojo/blanco)
     *  2. Actualización optimista del contador visible (+1 / -1)
     *  3. Confirma con Firestore
     *  4. Si falla → revierte ambos cambios
     */
    fun toggleLikeResena(resenaId: Int) {
        if (currentUserId.isBlank()) return

        val resena = _uiState.value.resenas.find { it.id == resenaId } ?: return
        if (resena.firestoreDocId.isBlank()) return

        val yaLikeado = resenaId in _uiState.value.resenasLikeadas

        viewModelScope.launch {
            // Actualización optimista — icono y contador
            _uiState.update { state ->
                val nuevosLiked = if (yaLikeado)
                    state.resenasLikeadas - resenaId
                else
                    state.resenasLikeadas + resenaId

                // Actualiza el contador en la lista de reseñas
                val resenasActualizadas = state.resenas.map { r ->
                    if (r.id == resenaId) {
                        r.copy(likes = if (yaLikeado) (r.likes - 1).coerceAtLeast(0)
                        else r.likes + 1)
                    } else r
                }

                state.copy(
                    resenasLikeadas = nuevosLiked,
                    resenas         = resenasActualizadas
                )
            }

            // Confirmar con Firestore
            likeRepository.toggleLike(resena.firestoreDocId, currentUserId)
                .onFailure {
                    // Revertir si falló
                    _uiState.update { state ->
                        val revertidos = if (yaLikeado)
                            state.resenasLikeadas + resenaId
                        else
                            state.resenasLikeadas - resenaId

                        val resenasRevertidas = state.resenas.map { r ->
                            if (r.id == resenaId) {
                                r.copy(likes = if (yaLikeado) r.likes + 1
                                else (r.likes - 1).coerceAtLeast(0))
                            } else r
                        }

                        state.copy(
                            resenasLikeadas = revertidos,
                            resenas         = resenasRevertidas
                        )
                    }
                }
        }
    }
}
