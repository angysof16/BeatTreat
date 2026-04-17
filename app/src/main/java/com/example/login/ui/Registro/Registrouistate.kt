// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/Registro/RegistroUIState.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.Registro

data class RegistroUIState(
    val email: String           = "",
    val password: String        = "",
    val nombre: String          = "",
    val username: String        = "",
    val country: String         = "",
    val bio: String             = "",
    val selectedTab: Int        = 1,
    val registroExitoso: Boolean = false,
    val isLoading: Boolean      = false,
    val errorMessage: String?   = null
)
