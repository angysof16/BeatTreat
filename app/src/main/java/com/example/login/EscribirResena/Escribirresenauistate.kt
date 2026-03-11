package com.example.login.EscribirResena

data class EscribirResenaUIState(
    val textoResena: String = "",
    val calificacion: Float = 0f,
    val albumSeleccionado: String = "",
    val publicadoExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)