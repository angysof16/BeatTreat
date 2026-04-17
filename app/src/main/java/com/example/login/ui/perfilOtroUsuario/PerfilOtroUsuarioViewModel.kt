// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/perfilOtroUsuario/PerfilOtroUsuarioViewModel.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.PerfilOtroUsuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilOtroUsuarioViewModel @Inject constructor(
    private val firestoreUserRepository: FirestoreUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilOtroUsuarioUIState())
    val uiState: StateFlow<PerfilOtroUsuarioUIState> = _uiState.asStateFlow()

    /**
     * Carga el perfil de otro usuario desde Firestore.
     * Recibe el userId como String (UID de Firebase Auth).
     */
    fun cargarPerfil(userId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = firestoreUserRepository.getUserById(userId)
            if (result.isSuccess) {
                val dto = result.getOrNull()!!
                _uiState.update {
                    it.copy(
                        usuario   = OtroUsuarioUI(
                            id            = userId.hashCode(),
                            nombre        = dto.name.ifBlank { "Usuario" },
                            username      = "@${dto.username}",
                            bio           = dto.bio ?: "",
                            fotoPerfilUrl = dto.profileImage ?: ""
                        ),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Usuario no encontrado"
                    )
                }
            }
        }
    }

    // Mantiene compatibilidad con el código que pasa Int (convierte a String)
    fun cargarPerfil(userId: Int) {
        cargarPerfil(userId.toString())
    }
}
