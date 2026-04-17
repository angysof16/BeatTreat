// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/EditarPerfil/EditarPerfilViewModel.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.EditarPerfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreUserRepository
import com.example.login.data.repository.StorageRepository
import com.example.login.ui.Perfil.PerfilData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarPerfilViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val firestoreUserRepository: FirestoreUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPerfilUIState())
    val uiState: StateFlow<EditarPerfilUIState> = _uiState.asStateFlow()

    // Cargamos desde Firestore al abrir la pantalla
    init {
        cargarDatosActuales()
    }

    private fun cargarDatosActuales() {
        viewModelScope.launch {
            val result = firestoreUserRepository.getMyProfile()
            val perfil = result.getOrElse { PerfilData.perfilActual }
            _uiState.update {
                it.copy(
                    nombre        = perfil.nombre,
                    usuario       = perfil.usuario.removePrefix("@"),
                    fotoPerfilUrl = perfil.fotoPerfilUrl
                )
            }
        }
    }

    fun onNombreChange(valor: String)  { _uiState.update { it.copy(nombre  = valor) } }
    fun onUsuarioChange(valor: String) { _uiState.update { it.copy(usuario = valor) } }
    fun onBioChange(valor: String)     { _uiState.update { it.copy(bio     = valor) } }

    fun subirFotoPerfil(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, errorMessage = null) }

            val result = storageRepository.uploadProfileImage(uri)

            if (result.isSuccess) {
                val nuevaUrl = result.getOrNull() ?: ""
                PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = nuevaUrl)
                _uiState.update { it.copy(fotoPerfilUrl = nuevaUrl, isUploadingPhoto = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isUploadingPhoto = false,
                        errorMessage     = result.exceptionOrNull()?.message
                            ?: "No se pudo subir la foto."
                    )
                }
            }
        }
    }

    fun guardar() {
        val state = _uiState.value
        if (state.nombre.isBlank() || state.usuario.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nombre y usuario son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = firestoreUserRepository.updateProfile(
                name         = state.nombre,
                username     = state.usuario,
                bio          = state.bio.ifBlank { null },
                profileImage = state.fotoPerfilUrl.ifBlank { null }
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(guardadoExitoso = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
                    )
                }
            }
        }
    }

    fun resetGuardado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }
}
