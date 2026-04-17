// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/Registro/RegistroViewModel.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.Registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.example.login.data.repository.FirestoreUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreUserRepository: FirestoreUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUIState())
    val uiState: StateFlow<RegistroUIState> = _uiState.asStateFlow()

    fun onEmailChange(email: String)       { _uiState.update { it.copy(email    = email) } }
    fun onPasswordChange(password: String) { _uiState.update { it.copy(password = password) } }
    fun onNombreChange(name: String)       { _uiState.update { it.copy(nombre   = name) } }
    fun onUsernameChange(username: String) { _uiState.update { it.copy(username = username) } }
    fun onCountryChange(country: String)   { _uiState.update { it.copy(country  = country) } }
    fun onBioChange(bio: String)           { _uiState.update { it.copy(bio      = bio) } }

    fun onTabChange(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun registrar() {
        val state = _uiState.value

        when {
            state.email.isBlank() || state.password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Completa email y contraseña") }
                return
            }
            state.nombre.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Ingresa tu nombre") }
                return
            }
            state.username.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Ingresa un nombre de usuario") }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            // Paso 1: crear cuenta en Firebase Auth
            val authResult = authRepository.signUp(state.email, state.password)

            if (authResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = authResult.exceptionOrNull()?.message ?: "Error al registrar"
                    )
                }
                return@launch
            }

            // Paso 2: guardar datos adicionales en Firestore
            val firestoreResult = firestoreUserRepository.registerUser(
                name     = state.nombre,
                username = state.username,
                country  = state.country.ifBlank { null },
                bio      = state.bio.ifBlank { null }
            )

            if (firestoreResult.isFailure) {
                // Auth fue exitoso pero Firestore falló; aun así dejamos pasar
                android.util.Log.e(
                    "RegistroVM",
                    "Firestore error: ${firestoreResult.exceptionOrNull()?.message}"
                )
            }

            _uiState.update { it.copy(registroExitoso = true, isLoading = false) }
        }
    }

    fun resetRegistroExitoso() {
        _uiState.update { it.copy(registroExitoso = false) }
    }
}
