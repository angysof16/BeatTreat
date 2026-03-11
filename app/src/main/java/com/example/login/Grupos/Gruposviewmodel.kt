package com.example.login.Grupos

import androidx.lifecycle.ViewModel
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
                grupos    = GrupoChatData.grupos,
                isLoading = false
            )
        }
    }
}