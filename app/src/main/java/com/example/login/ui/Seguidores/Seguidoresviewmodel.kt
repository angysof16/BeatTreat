package com.example.login.ui.Seguidores

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SeguidoresViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SeguidoresUIState())
    val uiState: StateFlow<SeguidoresUIState> = _uiState.asStateFlow()

    // Datos de ejemplo
    private val listaSiguiendo = listOf(
        UsuarioUI(1, "María García",    "@mariagrck"),
        UsuarioUI(2, "Carlos Ruiz",     "@carlosrz"),
        UsuarioUI(3, "Laura Pérez",     "@laurapz"),
        UsuarioUI(4, "Diego Torres",    "@diegot"),
        UsuarioUI(5, "Ana Martínez",    "@anamtz")
    )

    private val listaSeguidores = listOf(
        UsuarioUI(6,  "Juan López",     "@juanl"),
        UsuarioUI(7,  "Sofía Romero",   "@sofiar"),
        UsuarioUI(8,  "Pedro Díaz",     "@pedrod"),
        UsuarioUI(1,  "María García",   "@mariagrck")
    )

    fun cargar(tipo: String) {
        val usuarios = if (tipo == "siguiendo") listaSiguiendo else listaSeguidores
        _uiState.update {
            it.copy(
                tipo       = tipo,
                usuarios   = usuarios,
                // En "siguiendo" todos ya están siendo seguidos
                siguiendoIds = if (tipo == "siguiendo") usuarios.map { u -> u.id }.toSet() else emptySet(),
                isLoading  = false
            )
        }
    }

    fun toggleSeguir(usuarioId: Int) {
        _uiState.update { state ->
            val ids = state.siguiendoIds
            state.copy(siguiendoIds = if (usuarioId in ids) ids - usuarioId else ids + usuarioId)
        }
    }
}