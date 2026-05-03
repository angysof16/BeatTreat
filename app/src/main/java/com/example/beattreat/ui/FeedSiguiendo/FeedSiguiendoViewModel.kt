package com.example.beattreat.ui.FeedSiguiendo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.FollowRepository
import com.example.beattreat.data.repository.FirestoreReviewLiveRepository
import com.example.beattreat.data.repository.ReviewLikeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel del feed "Siguiendo".
 *
 * Flujo (exactamente como el profesor explicó):
 *   1. Al iniciar, obtiene la lista de IDs de usuarios que sigo.
 *   2. Llama a listenFeedByAuthors → retorna un Flow en tiempo real.
 *   3. Usa viewModelScope.launch + collect para escuchar el Flow.
 *   4. Cada vez que llegan nuevos datos, actualiza el estado.
 *   5. Si hay error en la conexión, el .catch lo captura.
 *
 * Sobre el manejo de errores (dicho por el profesor):
 *   "No hay throws, entonces no es necesario hacer try-catch.
 *    Lo que sí puede pasar es que el Flow falle a mitad de la conexión
 *    y por eso usamos .catch"
 */
@HiltViewModel
class FeedSiguiendoViewModel @Inject constructor(
    private val followRepository: FollowRepository,
    private val liveReviewRepository: FirestoreReviewLiveRepository,
    private val likeRepository: ReviewLikeRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedSiguiendoUIState())
    val uiState: StateFlow<FeedSiguiendoUIState> = _uiState.asStateFlow()

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    init {
        cargarFeed()
    }

    /**
     * Carga el feed en tiempo real:
     * 1. Obtiene IDs de usuarios que sigo.
     * 2. Suscribe al Flow de reviews de esos usuarios.
     */
    fun cargarFeed() {
        if (currentUserId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Debes iniciar sesión") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Paso 1: obtener IDs de usuarios que sigo
            val followingResult = followRepository.getFollowingIds(currentUserId)

            if (followingResult.isFailure) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = followingResult.exceptionOrNull()?.message)
                }
                return@launch
            }

            val followingIds = followingResult.getOrDefault(emptyList())

            if (followingIds.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, sinSeguidos = true, reviews = emptyList()) }
                return@launch
            }

            // Paso 2: suscribir al Flow de reviews en tiempo real
            // El profesor dijo: no hay try-catch, usamos .catch para errores
            liveReviewRepository.listenFeedByAuthors(followingIds)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Error en la conexión")
                    }
                }
                .collect { reviews ->
                    // Cargar estado de likes para las reviews recibidas
                    val likedIds = reviews.mapNotNull { review ->
                        if (review.firestoreDocId.isNotBlank()) {
                            val liked = likeRepository.isLikedBy(review.firestoreDocId, currentUserId)
                                .getOrNull() ?: false
                            if (liked) review.firestoreDocId else null
                        } else null
                    }.toSet()

                    _uiState.update { state ->
                        state.copy(
                            reviews    = reviews,
                            isLoading  = false,
                            sinSeguidos = false,
                            likedReviewIds = likedIds
                        )
                    }
                }
        }
    }

    /**
     * Da o quita like a una review.
     * Actualiza UI optimistamente (antes de confirmar con Firestore).
     */
    fun toggleLike(firestoreDocId: String) {
        if (currentUserId.isBlank() || firestoreDocId.isBlank()) return

        viewModelScope.launch {
            // Actualización optimista: cambia el estado en UI antes de esperar Firestore
            val yaLikeado = firestoreDocId in _uiState.value.likedReviewIds
            _uiState.update { state ->
                val nuevosLiked = if (yaLikeado) {
                    state.likedReviewIds - firestoreDocId
                } else {
                    state.likedReviewIds + firestoreDocId
                }
                state.copy(likedReviewIds = nuevosLiked)
            }

            // Confirmar con Firestore (la transacción actualiza el contador)
            likeRepository.toggleLike(firestoreDocId, currentUserId)
                .onFailure {
                    // Revertir si falló
                    _uiState.update { state ->
                        val revertidos = if (yaLikeado) {
                            state.likedReviewIds + firestoreDocId
                        } else {
                            state.likedReviewIds - firestoreDocId
                        }
                        state.copy(likedReviewIds = revertidos)
                    }
                }
        }
    }
}
