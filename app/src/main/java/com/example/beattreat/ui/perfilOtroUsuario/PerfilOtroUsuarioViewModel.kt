package com.example.beattreat.ui.PerfilOtroUsuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.FollowRepository
import com.example.beattreat.data.repository.FirestoreReviewRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FIX Sprint 3 — Bug #2
 *
 * Problema original: el estado optimista se aplicaba, pero si Firestore
 * retornaba un resultado diferente al esperado (por ejemplo, el campo no
 * existía y la transacción fallaba silenciosamente), el estado de la UI
 * quedaba inconsistente con Firestore.
 *
 * Solución:
 *  1. Después de que Firestore confirma el resultado, se usa el valor
 *     REAL que retornó la transacción (true/false) para actualizar la UI,
 *     no el valor predicho optimistamente.
 *  2. Después del follow/unfollow, se vuelven a leer los contadores reales
 *     de Firestore para asegurar que lo que se muestra es lo correcto.
 */
@HiltViewModel
class PerfilOtroUsuarioViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val followRepository: FollowRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilOtroUsuarioUIState())
    val uiState: StateFlow<PerfilOtroUsuarioUIState> = _uiState.asStateFlow()

    private var targetUserId: String = ""

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    fun cargarPerfil(userId: String) {
        targetUserId = userId
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val perfilDeferred      = async { firestoreUserRepository.getUserById(userId) }
            val reviewsDeferred     = async { firestoreReviewRepository.getReviewsByUser(userId) }
            val isFollowingDeferred = async {
                if (currentUserId.isNotBlank() && currentUserId != userId)
                    followRepository.isFollowing(currentUserId, userId).getOrNull() ?: false
                else false
            }
            val followersDeferred = async { followRepository.getFollowersCount(userId).getOrNull() ?: 0 }
            val followingDeferred = async { followRepository.getFollowingCount(userId).getOrNull() ?: 0 }

            val perfilResult   = perfilDeferred.await()
            val reviewsResult  = reviewsDeferred.await()
            val isFollowing    = isFollowingDeferred.await()
            val followersCount = followersDeferred.await()
            val followingCount = followingDeferred.await()

            if (perfilResult.isSuccess) {
                val dto = perfilResult.getOrNull()!!
                val reviews = if (reviewsResult.isSuccess) {
                    reviewsResult.getOrDefault(emptyList()).map { resena ->
                        ReviewOtroUsuarioUI(
                            id           = resena.id.hashCode(),
                            albumNombre  = resena.albumNombre.ifBlank { "Álbum" },
                            albumArtista = resena.albumArtista,
                            rating       = resena.calificacion,
                            contenido    = resena.texto,
                            fecha        = resena.fecha
                        )
                    }
                } else emptyList()

                _uiState.update {
                    it.copy(
                        usuario = OtroUsuarioUI(
                            id             = userId.hashCode(),
                            nombre         = dto.name.ifBlank { "Usuario" },
                            username       = "@${dto.username}",
                            bio            = dto.bio ?: "",
                            fotoPerfilUrl  = dto.profileImage ?: "",
                            followersCount = followersCount,
                            followingCount = followingCount
                        ),
                        reviews      = reviews,
                        isFollowing  = isFollowing,
                        isLoading    = false,
                        puedeFollow  = currentUserId.isNotBlank() && currentUserId != userId,
                        errorMessage = if (reviewsResult.isFailure) "No se pudieron cargar las reseñas" else null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = perfilResult.exceptionOrNull()?.message ?: "Usuario no encontrado"
                    )
                }
            }
        }
    }

    /**
     * FIX: ya no usa actualización optimista que podía quedar desfasada.
     *
     * Flujo corregido:
     *  1. Deshabilita el botón mientras procesa (isFollowLoading = true)
     *  2. Llama a Firestore y espera el resultado REAL
     *  3. Actualiza UI con el estado real que retornó Firestore
     *  4. Vuelve a leer el contador de seguidores desde Firestore
     *
     * Esto garantiza que la UI siempre refleja lo que hay en la base de datos.
     */
    fun toggleFollow() {
        if (currentUserId.isBlank() || targetUserId.isBlank()) return
        if (_uiState.value.isFollowLoading) return  // evita doble tap

        viewModelScope.launch {
            // Deshabilitar botón mientras espera
            _uiState.update { it.copy(isFollowLoading = true) }

            val result = followRepository.followOrUnfollow(currentUserId, targetUserId)

            result.onSuccess { ahoraEstasSiguiendo ->
                // FIX: usa el valor REAL que retornó Firestore, no el predicho
                val nuevoContador = followRepository.getFollowersCount(targetUserId).getOrNull()
                    ?: (_uiState.value.usuario?.followersCount ?: 0)

                _uiState.update { state ->
                    state.copy(
                        isFollowing    = ahoraEstasSiguiendo,
                        isFollowLoading = false,
                        usuario        = state.usuario?.copy(
                            followersCount = nuevoContador
                        )
                    )
                }
            }.onFailure { error ->
                // Si falló, no cambia el estado de follow — muestra error
                _uiState.update {
                    it.copy(
                        isFollowLoading = false,
                        errorMessage    = "Error al procesar: ${error.message}"
                    )
                }
            }
        }
    }

    fun cargarPerfil(userId: Int) = cargarPerfil(userId.toString())
}
