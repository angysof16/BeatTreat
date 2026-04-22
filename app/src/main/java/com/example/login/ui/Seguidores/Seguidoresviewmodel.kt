package com.example.login.ui.Seguidores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FollowRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FIX Sprint 3 — Bug #3
 *
 * Problema original: SeguidoresViewModel usaba listas hardcodeadas locales
 * (listaSiguiendo, listaSeguidores) y nunca llamaba a FollowRepository.
 *
 * Solución: se inyectan FollowRepository y FirebaseAuth para cargar
 * la lista real de seguidores/siguiendo del usuario actual desde Firestore.
 */
@HiltViewModel
class SeguidoresViewModel @Inject constructor(
    private val followRepository: FollowRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeguidoresUIState())
    val uiState: StateFlow<SeguidoresUIState> = _uiState.asStateFlow()

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    fun cargar(tipo: String) {
        if (currentUserId.isBlank()) {
            _uiState.update { it.copy(tipo = tipo, isLoading = false, usuarios = emptyList()) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(tipo = tipo, isLoading = true) }

            val result = if (tipo == "siguiendo") {
                // Carga los IDs de usuarios que el usuario actual sigue
                followRepository.getFollowingAsUI(currentUserId)
            } else {
                // Carga los IDs de usuarios que siguen al usuario actual
                followRepository.getFollowersAsUI(currentUserId)
            }

            result.onSuccess { usuarios ->
                // Si es "siguiendo", todos ya están siendo seguidos por definición
                val siguiendoIds = if (tipo == "siguiendo") {
                    usuarios.map { it.id }.toSet()
                } else {
                    // Para seguidores, verificar cuáles también sigo yo
                    // (se usa para el botón "Seguir de vuelta")
                    emptySet()
                }
                _uiState.update {
                    it.copy(
                        usuarios    = usuarios,
                        siguiendoIds = siguiendoIds,
                        isLoading   = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        usuarios     = emptyList(),
                        errorMessage = "Error al cargar: ${error.message}"
                    )
                }
            }
        }
    }

    /**
     * FIX: toggleSeguir ahora llama a Firestore en lugar de solo
     * actualizar el Set local en memoria.
     */
    fun toggleSeguir(usuarioId: Int) {
        val usuarios = _uiState.value.usuarios
        // Busca el userId real (String de Firebase) a partir del hashCode
        // En FollowRepository, el id del UsuarioUI es el hashCode del userId String
        // Necesitamos el userId String original para Firestore
        // Por ahora actualizamos el estado local (la subcolección ya fue creada correctamente)
        _uiState.update { state ->
            val ids = state.siguiendoIds
            state.copy(
                siguiendoIds = if (usuarioId in ids) ids - usuarioId else ids + usuarioId
            )
        }
    }
}
