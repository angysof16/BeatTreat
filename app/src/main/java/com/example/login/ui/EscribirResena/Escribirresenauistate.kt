package com.example.login.ui.EscribirResena

data class EscribirResenaUIState(
    val textoResena: String = "",
    val calificacion: Float = 0f,
    val albumSeleccionado: String = "",
    val albumId: Int = 0,              // ← AGREGAR
    val publicadoExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)