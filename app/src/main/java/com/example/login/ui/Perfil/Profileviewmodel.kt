package com.example.login.ui.Perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.CURRENT_USER_ID
import com.example.login.data.repository.AuthRepository
import com.example.login.data.repository.MiPerfilRepository
import com.example.login.data.repository.StorageRepository
import com.example.login.ui.MiPerfil.MiResenaUI
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
    private val firebaseAuth: FirebaseAuth,
    private val miPerfilRepository: MiPerfilRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
        cargarResenasDelBackend()
    }

    private fun cargarPerfil() {
        val urlDeFirebaseAuth = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""

        if (urlDeFirebaseAuth.isNotBlank()) {
            PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = urlDeFirebaseAuth)
        } else {
            PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = "")
        }

        _uiState.update {
            it.copy(
                perfil           = PerfilData.perfilActual,
                albumesFavoritos = PerfilData.albumesFavoritos,
                isLoading        = false
            )
        }
    }

    private fun cargarResenasDelBackend() {
        viewModelScope.launch {
            val result = miPerfilRepository.getMisResenas()
            result.onSuccess { lista ->
                // Convertir MiResenaUI -> ResenaUI para mostrar en la pantalla de perfil
                val resenasUI = lista.take(3).map { resena ->
                    ResenaUI(
                        id           = resena.id,
                        autorNombre  = PerfilData.perfilActual.nombre,
                        autorUsuario = PerfilData.perfilActual.usuario,
                        autorFotoUrl = PerfilData.perfilActual.fotoPerfilUrl,
                        texto        = resena.content,
                        likes        = 0,
                        comentarios  = 0
                    )
                }
                _uiState.update { it.copy(resenas = resenasUI) }
            }
            // Si falla la carga simplemente no mostramos reseñas (lista vacía, sin fallback hardcodeado)
        }
    }

    fun refrescarPerfil() {
        cargarPerfil()
        cargarResenasDelBackend()
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