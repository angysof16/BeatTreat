package com.example.login.EditarPerfil

import androidx.lifecycle.ViewModel
import com.example.login.Perfil.PerfilData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditarPerfilViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPerfilUIState())
    val uiState: StateFlow<EditarPerfilUIState> = _uiState.asStateFlow()

    init {
        // Pre-carga los datos actuales del perfil
        val perfil = PerfilData.perfilActual
        _uiState.update {
            it.copy(
                nombre  = perfil.nombre,
                usuario = perfil.usuario.removePrefix("@")
            )
        }
    }

    fun onNombreChange(valor: String)   { _uiState.update { it.copy(nombre  = valor) } }
    fun onUsuarioChange(valor: String)  { _uiState.update { it.copy(usuario = valor) } }
    fun onBioChange(valor: String)      { _uiState.update { it.copy(bio     = valor) } }

    fun guardar() {
        // En una app real aquí se llamaría al repositorio
        _uiState.update { it.copy(guardadoExitoso = true) }
    }

    fun resetGuardado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }
}