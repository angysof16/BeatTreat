// SeguidoresViewModel.kt
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
 * FIX Sprint 3 — Bug #3 (completo)
 *
 * Problema original: 
 *   - UsuarioUI solo guardaba Int (hashCode) y no el firestoreId real
 *   - toggleSeguir nunca llamaba a Firestore
 *
 * Solución completa:
 *   1. UsuarioUI ahora guarda firestoreId (UID real de Firebase)
 *   2. siguiendoIds es Set<String> con los UIDs reales
 *   3. toggleSeguir llama a followRepository.followOrUnfollow con los UIDs reales
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
            _uiState.update {
                it.copy(tipo = tipo, isLoading = false, usuarios = emptyList())
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(tipo = tipo, isLoading = true, errorMessage = null) }

            val result = if (tipo == "siguiendo") {
                followRepository.getFollowingAsUI(currentUserId)
            } else {
                followRepository.getFollowersAsUI(currentUserId)
            }

            result.onSuccess { usuarios ->
                // Para "siguiendo", todos ya están siendo seguidos por definición
                val siguiendoIds = if (tipo == "siguiendo") {
                    usuarios.map { it.firestoreId }.toSet()  // ← usa firestoreId real
                } else {
                    // Para seguidores, necesitamos ver cuáles de ellos YA sigo
                    // Cargamos los IDs que sigo actualmente
                    val myFollowingIds = followRepository.getFollowingIds(currentUserId)
                        .getOrDefault(emptyList())
                        .toSet()
                    myFollowingIds
                }

                _uiState.update {
                    it.copy(
                        usuarios     = usuarios,
                        siguiendoIds = siguiendoIds,
                        isLoading    = false,
                        errorMessage = null
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
     * FIX: toggleSeguir ahora llama a Firestore correctamente
     * usando el firestoreId real del usuario.
     */
    fun toggleSeguir(usuarioFirestoreId: String) {
        if (currentUserId.isBlank() || usuarioFirestoreId.isBlank()) return

        viewModelScope.launch {
            // Llamar a Firestore con los UIDs reales
            val result = followRepository.followOrUnfollow(currentUserId, usuarioFirestoreId)

            result.onSuccess { ahoraEstasSiguiendo ->
                // Actualizar el estado local con el resultado REAL
                _uiState.update { state ->
                    val nuevosSiguiendoIds = if (ahoraEstasSiguiendo) {
                        state.siguiendoIds + usuarioFirestoreId
                    } else {
                        state.siguiendoIds - usuarioFirestoreId
                    }
                    state.copy(siguiendoIds = nuevosSiguiendoIds)
                }

                // Nota: No necesitamos recargar toda la lista porque los contadores
                // se actualizarán cuando el usuario vuelva a abrir la pantalla
                // o cuando refresque manualmente
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Error al segu@r: ${error.message}")
                }
            }
        }
    }
}