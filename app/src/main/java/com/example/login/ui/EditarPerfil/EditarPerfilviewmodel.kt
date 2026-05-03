// ui/EditarPerfil/EditarPerfilViewModel.kt
package com.example.login.ui.EditarPerfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreUserRepository
import com.example.login.data.repository.StorageRepository
import com.example.login.ui.Perfil.PerfilData
import com.google.firebase.auth.FirebaseAuth
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
    private val firestoreUserRepository: FirestoreUserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPerfilUIState())
    val uiState: StateFlow<EditarPerfilUIState> = _uiState.asStateFlow()

    init {
        cargarDatosActuales()
    }

    private fun cargarDatosActuales() {
        viewModelScope.launch {
            val result = firestoreUserRepository.getMyProfile()
            val perfil = result.getOrElse { PerfilData.perfilActual }
            _uiState.update {
                it.copy(
                    nombre = perfil.nombre,
                    usuario = perfil.usuario.removePrefix("@"),
                    bio = perfil.bio,
                    fotoPerfilUrl = perfil.fotoPerfilUrl
                )
            }
        }
    }

    fun onNombreChange(valor: String) { _uiState.update { it.copy(nombre = valor) } }
    fun onUsuarioChange(valor: String) { _uiState.update { it.copy(usuario = valor) } }
    fun onBioChange(valor: String) { _uiState.update { it.copy(bio = valor) } }

    fun subirFotoPerfil(uri: Uri) {
        viewModelScope.launch {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                _uiState.update {
                    it.copy(
                        isUploadingPhoto = false,
                        errorMessage = "Debes iniciar sesión para cambiar tu foto"
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isUploadingPhoto = true, errorMessage = null) }

            val result = storageRepository.uploadProfileImage(uri)

            if (result.isSuccess) {
                val nuevaUrl = result.getOrNull() ?: ""
                PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = nuevaUrl)
                _uiState.update {
                    it.copy(
                        fotoPerfilUrl = nuevaUrl,
                        isUploadingPhoto = false
                    )
                }
            } else {
                val error = result.exceptionOrNull()
                _uiState.update {
                    it.copy(
                        isUploadingPhoto = false,
                        errorMessage = error?.message ?: "No se pudo subir la foto"
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
                name = state.nombre,
                username = state.usuario,
                bio = state.bio.ifBlank { null },
                profileImage = state.fotoPerfilUrl.ifBlank { null }
            )

            if (result.isSuccess) {
                // Actualizar PerfilData global
                PerfilData.perfilActual = PerfilData.perfilActual.copy(
                    nombre = state.nombre,
                    usuario = "@${state.usuario}",
                    bio = state.bio
                )

                _uiState.update { it.copy(guardadoExitoso = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
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