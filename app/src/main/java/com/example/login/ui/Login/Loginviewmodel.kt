package com.example.login.ui.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.example.login.util.FcmTokenHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onRememberMeChange(checked: Boolean) {
        _uiState.update { it.copy(rememberMe = checked) }
    }

    fun onTabChange(tab: Int) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                irARegistro = tab == 1
            )
        }
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.signIn(state.email, state.password)
            if (result.isSuccess) {

                FcmTokenHelper.registrarToken()

                _uiState.update { it.copy(loginExitoso = true, isLoading = false) }
            } else {
                val mensaje = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                _uiState.update { it.copy(errorMessage = mensaje, isLoading = false) }
            }
        }
    }

    fun resetLoginExitoso() {
        _uiState.update { it.copy(loginExitoso = false) }
    }

    fun resetIrARegistro() {
        _uiState.update { it.copy(irARegistro = false) }
    }
}