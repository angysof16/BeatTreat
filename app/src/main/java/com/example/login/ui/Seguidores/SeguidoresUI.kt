package com.example.login.ui.Seguidores

data class UsuarioUI(val id: Int, val nombre: String, val usuario: String)

data class SeguidoresUIState(
    val tipo: String = "seguidores",
    val usuarios: List<UsuarioUI> = emptyList(),
    val siguiendoIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false
)