// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/Perfil/ProfileViewModel.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.Perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.example.login.data.repository.FirestoreUserRepository
import com.example.login.data.repository.StorageRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val firestoreUserRepository: FirestoreUserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            // Intentamos cargar desde Firestore
            val result = firestoreUserRepository.getMyProfile()
            if (result.isSuccess) {
                val perfil = result.getOrNull()!!
                _uiState.update {
                    it.copy(
                        perfil           = perfil,
                        albumesFavoritos = PerfilData.albumesFavoritos,
                        resenas          = PerfilData.resenasRecientes,
                        isLoading        = false
                    )
                }
            } else {
                // Fallback: usamos datos locales y mostramos error sutil
                val urlFirebaseAuth = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""
                if (urlFirebaseAuth.isNotBlank()) {
                    PerfilData.perfilActual =
                        PerfilData.perfilActual.copy(fotoPerfilUrl = urlFirebaseAuth)
                }
                _uiState.update {
                    it.copy(
                        perfil           = PerfilData.perfilActual,
                        albumesFavoritos = PerfilData.albumesFavoritos,
                        resenas          = PerfilData.resenasRecientes,
                        isLoading        = false,
                        errorMessage     = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun refrescarFotoPerfil() {
        val urlFirebaseAuth = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""
        val urlFinal = urlFirebaseAuth.ifBlank { PerfilData.perfilActual.fotoPerfilUrl }
        _uiState.update { state ->
            state.copy(perfil = state.perfil?.copy(fotoPerfilUrl = urlFinal))
        }
    }

    fun cerrarSesion() {
        PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = "")
        authRepository.signOut()
        _uiState.update { it.copy(cerrarSesionExitoso = true) }
    }

    fun resetCerrarSesion() {
        _uiState.update { it.copy(cerrarSesionExitoso = false) }
    }
}
