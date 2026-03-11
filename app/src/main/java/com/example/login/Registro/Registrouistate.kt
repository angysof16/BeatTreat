package com.example.login.Registro

data class RegistroUIState(
    val email: String = "",
    val password: String = "",
    val selectedTab: Int = 1,
    val registroExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)