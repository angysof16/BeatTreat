package com.example.beattreat.ui.Grupos

import androidx.lifecycle.ViewModel
import com.example.beattreat.ui.Perfil.PerfilData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GruposViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(GruposUIState())
    val uiState: StateFlow<GruposUIState> = _uiState.asStateFlow()

    init {
        cargarGrupos()
    }

    private fun cargarGrupos() {
        _uiState.update {
            it.copy(
                grupos        = GrupoChatData.grupos,
                fotoPerfilUrl = PerfilData.perfilActual.fotoPerfilUrl,
                isLoading     = false
            )
        }
    }

    // Llamado desde AppNavegacion en ON_RESUME para reflejar cambios de foto
    fun refrescarFotoPerfil() {
        _uiState.update { it.copy(fotoPerfilUrl = PerfilData.perfilActual.fotoPerfilUrl) }
    }
}