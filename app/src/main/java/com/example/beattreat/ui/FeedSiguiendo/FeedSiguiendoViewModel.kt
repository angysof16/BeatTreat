// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/FeedSiguiendo/FeedSiguiendoViewModel.kt
// CAMBIOS Sprint 4:
//   - Suscripción paralela al Flow de ReviewMapItem para la vista de mapa.
//   - switchVista(): alterna entre LISTA y MAPA.
//   - onMapItemSelected(): actualiza el marcador seleccionado.
//   - onMapItemDismissed(): cierra el InfoWindow.
// ──────────────────────────────────────────────────────────────────────────────
package com.example.beattreat.ui.FeedSiguiendo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.FollowRepository
import com.example.beattreat.data.repository.FirestoreReviewLiveRepository
import com.example.beattreat.data.repository.ReviewLikeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
ViewModel del feed "Siguiendo".

además del Flow de la lista de reviews, suscribe al Flow del mapa
([FirestoreReviewLiveRepository.listenFeedMapItems]) para mostrar
marcadores de reviews de las últimas 24h con coordenadas.

Ambos Flows comparten el mismo conjunto de authorIds y se cancelan
automáticamente cuando el ViewModel se destruye (viewModelScope).
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

    /** Job del Flow de la lista — se cancela antes de relanzar */
    private var listJob: Job? = null

    /** Job del Flow del mapa — se cancela antes de relanzar */
    private var mapJob: Job? = null

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    init {
        cargarFeed()
    }

    // ── Carga inicial ─────────────────────────────────────────────────────────

    /**
     * Obtiene los IDs de usuarios que sigo y suscribe a ambos Flows
     * (lista y mapa) de forma paralela.
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

            // Paso 2: suscribir al Flow de lista y al Flow de mapa en paralelo
            suscribirListaFlow(followingIds)
            suscribirMapaFlow(followingIds)
        }
    }

    // ── Flows ─────────────────────────────────────────────────────────────────

    private fun suscribirListaFlow(followingIds: List<String>) {
        listJob?.cancel()
        listJob = viewModelScope.launch {
            liveReviewRepository.listenFeedByAuthors(followingIds)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Error en la conexión")
                    }
                }
                .collect { reviews ->
                    val likedIds = reviews.mapNotNull { review ->
                        if (review.firestoreDocId.isNotBlank()) {
                            val liked = likeRepository.isLikedBy(review.firestoreDocId, currentUserId)
                                .getOrNull() ?: false
                            if (liked) review.firestoreDocId else null
                        } else null
                    }.toSet()

                    _uiState.update { state ->
                        state.copy(
                            reviews         = reviews,
                            isLoading       = false,
                            sinSeguidos     = false,
                            likedReviewIds  = likedIds
                        )
                    }
                }
        }
    }

    /**
     * Suscribe al Flow del mapa en tiempo real.
     *
     * El repositorio ya filtra por:
     *   - createdAt en las últimas 24h
     *   - latitude y longitude no nulos
     *
     * El ViewModel solo necesita guardar el resultado en [FeedSiguiendoUIState.reviewsMapItems].
     */
    private fun suscribirMapaFlow(followingIds: List<String>) {
        mapJob?.cancel()
        _uiState.update { it.copy(isMapLoading = true) }

        mapJob = viewModelScope.launch {
            liveReviewRepository.listenFeedMapItems(followingIds)
                .catch { e ->
                    // Error no crítico: el mapa simplemente no muestra marcadores
                    _uiState.update { it.copy(isMapLoading = false) }
                }
                .collect { mapItems ->
                    _uiState.update { it.copy(reviewsMapItems = mapItems, isMapLoading = false) }
                }
        }
    }

    // ── Sprint 4: control de vista ────────────────────────────────────────────

    /**
     * Alterna entre la vista de lista y la vista de mapa.
     * Al cambiar a MAPA se cierra cualquier InfoWindow abierto.
     */
    fun switchVista(vista: FeedVista) {
        _uiState.update { it.copy(vistaActual = vista, selectedMapItem = null) }
    }

    /**
     * Selecciona el marcador tocado por el usuario.
     * Abre el InfoWindow con la información del review.
     */
    fun onMapItemSelected(item: ReviewMapItem) {
        _uiState.update { it.copy(selectedMapItem = item) }
    }

    /**
     * Cierra el InfoWindow (toque fuera de un marcador).
     */
    fun onMapItemDismissed() {
        _uiState.update { it.copy(selectedMapItem = null) }
    }

    // ── Likes (existente) ─────────────────────────────────────────────────────

    fun toggleLike(firestoreDocId: String) {
        if (currentUserId.isBlank() || firestoreDocId.isBlank()) return

        viewModelScope.launch {
            val yaLikeado = firestoreDocId in _uiState.value.likedReviewIds
            // Actualización optimista
            _uiState.update { state ->
                state.copy(
                    likedReviewIds = if (yaLikeado)
                        state.likedReviewIds - firestoreDocId
                    else
                        state.likedReviewIds + firestoreDocId
                )
            }
            // Confirmar con Firestore
            likeRepository.toggleLike(firestoreDocId, currentUserId)
                .onFailure {
                    // Revertir si falló
                    _uiState.update { state ->
                        state.copy(
                            likedReviewIds = if (yaLikeado)
                                state.likedReviewIds + firestoreDocId
                            else
                                state.likedReviewIds - firestoreDocId
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listJob?.cancel()
        mapJob?.cancel()
    }
}