package com.example.login.ui.PerfilOtroUsuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreReviewRepository
import com.example.login.data.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilOtroUsuarioViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository,
    // ← NUEVO: inyectamos el repositorio de reviews
    private val firestoreReviewRepository: FirestoreReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilOtroUsuarioUIState())
    val uiState: StateFlow<PerfilOtroUsuarioUIState> = _uiState.asStateFlow()

    /**
     * Carga perfil Y reviews del usuario en paralelo.
     * Recibe el userId como String (UID de Firebase Auth).
     */
    fun cargarPerfil(userId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // Cargamos perfil y reviews en paralelo con async
            val perfilDeferred  = async { firestoreUserRepository.getUserById(userId) }
            val reviewsDeferred = async { firestoreReviewRepository.getReviewsByUser(userId) }

            val perfilResult  = perfilDeferred.await()
            val reviewsResult = reviewsDeferred.await()

            if (perfilResult.isSuccess) {
                val dto = perfilResult.getOrNull()!!

                // Mapeamos las reviews de Firestore a ReviewOtroUsuarioUI
                val reviews = if (reviewsResult.isSuccess) {
                    reviewsResult.getOrDefault(emptyList()).map { resena ->
                        ReviewOtroUsuarioUI(
                            id           = resena.id,
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
                        usuario   = OtroUsuarioUI(
                            id            = userId.hashCode(),
                            nombre        = dto.name.ifBlank { "Usuario" },
                            username      = "@${dto.username}",
                            bio           = dto.bio ?: "",
                            fotoPerfilUrl = dto.profileImage ?: ""
                        ),
                        reviews   = reviews,
                        isLoading = false,
                        errorMessage = if (reviewsResult.isFailure)
                            "No se pudieron cargar las reseñas" else null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = perfilResult.exceptionOrNull()?.message
                            ?: "Usuario no encontrado"
                    )
                }
            }
        }
    }

    // Mantiene compatibilidad con código que pasa Int
    fun cargarPerfil(userId: Int) {
        cargarPerfil(userId.toString())
    }
}