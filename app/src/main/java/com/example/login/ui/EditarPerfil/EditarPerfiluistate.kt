package com.example.login.ui.EditarPerfil

data class EditarPerfilUIState(
    val nombre: String = "",
    val usuario: String = "",
    val bio: String = "",
    val guardadoExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)