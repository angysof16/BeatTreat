package com.example.login.ui.PerfilOtroUsuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de perfil de otro usuario.
 *
 * Recibe el userId como parámetro de [cargarPerfil] (llamado desde
 * AppNavegacion con LaunchedEffect) y delega al [UserRepository].
 */
@HiltViewModel
class PerfilOtroUsuarioViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilOtroUsuarioUIState())
    val uiState: StateFlow<PerfilOtroUsuarioUIState> = _uiState.asStateFlow()

    /**
     * Carga en paralelo el perfil y los reviews del usuario con [userId].
     * Se llama desde AppNavegacion usando LaunchedEffect(userId).
     */
    fun cargarPerfil(userId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // Cargamos primero el perfil
            val perfilResult = userRepository.getUserById(userId)

            if (perfilResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = perfilResult.exceptionOrNull()?.message
                    )
                }
                return@launch
            }

            // Si el perfil cargó bien, cargamos los reviews
            val reviewsResult = userRepository.getReviewsByUser(userId)

            _uiState.update {
                it.copy(
                    usuario      = perfilResult.getOrNull(),
                    reviews      = reviewsResult.getOrDefault(emptyList()),
                    isLoading    = false,
                    errorMessage = if (reviewsResult.isFailure)
                        reviewsResult.exceptionOrNull()?.message
                    else null
                )
            }
        }
    }
}
