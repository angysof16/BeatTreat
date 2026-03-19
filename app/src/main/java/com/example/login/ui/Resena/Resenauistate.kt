package com.example.login.ui.Resena

data class ResenaUIState(
    val albumId: Int = 0,
    val albumNombre: String = "",        // para mostrar en el header de la pantalla
    val resenas: List<ResenaDetalladaUI> = emptyList(),
    val resenasLikeadas: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)