package com.example.login.ui.Registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUIState())
    val uiState: StateFlow<RegistroUIState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onTabChange(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun registrar() {
        val state = _uiState.value
        when {
            state.email.isBlank() || state.password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Completa todos los campos") }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.signUp(state.email, state.password)
            result.onSuccess {
                _uiState.update { it.copy(registroExitoso = true, isLoading = false) }
            }.onFailure { e ->
                val mensaje = when (e) {
                    is FirebaseAuthUserCollisionException -> "El correo ya está en uso"
                    is FirebaseAuthInvalidCredentialsException -> "El correo no es válido"
                    else -> e.message ?: "Error al registrar"
                }
                _uiState.update { it.copy(errorMessage = mensaje, isLoading = false) }
            }
        }
    }

    fun resetRegistroExitoso() {
        _uiState.update { it.copy(registroExitoso = false) }
    }
}