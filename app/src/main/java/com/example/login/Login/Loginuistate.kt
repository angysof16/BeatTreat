package com.example.login.Login

data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val selectedTab: Int = 0,
    val loginExitoso: Boolean = false,
    val irARegistro: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)