package com.example.login.ui.Perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
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
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        val urlDeFirebaseAuth = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""

        // Si es cuenta nueva, urlDeFirebaseAuth estará vacía y no pisamos nada
        if (urlDeFirebaseAuth.isNotBlank()) {
            PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = urlDeFirebaseAuth)
        } else {
            // ya no hay url residual de la sesion anterior
            PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = "")
        }

        _uiState.update {
            it.copy(
                perfil           = PerfilData.perfilActual,
                albumesFavoritos = PerfilData.albumesFavoritos,
                resenas          = PerfilData.resenasRecientes,
                isLoading        = false
            )
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