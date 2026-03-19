package com.example.login.ui.EditarPerfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPerfilUIState())
    val uiState: StateFlow<EditarPerfilUIState> = _uiState.asStateFlow()

    init {
        val perfil = PerfilData.perfilActual
        _uiState.update {
            it.copy(
                nombre        = perfil.nombre,
                usuario       = perfil.usuario.removePrefix("@"),
                fotoPerfilUrl = perfil.fotoPerfilUrl
            )
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

                // ── Guardamos la URL nueva en PerfilData (object singleton) ──
                // Así cuando ProfileViewModel llame a refrescarFotoPerfil()
                // leerá la URL actualizada y la mostrará en el perfil.
                PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = nuevaUrl)

                _uiState.update {
                    it.copy(fotoPerfilUrl = nuevaUrl, isUploadingPhoto = false)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isUploadingPhoto = false,
                        errorMessage     = "No se pudo subir la foto. Intenta de nuevo."
                    )
                }
            }
        }
    }

    fun guardar() {
        _uiState.update { it.copy(guardadoExitoso = true) }
    }

    fun resetGuardado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }
}