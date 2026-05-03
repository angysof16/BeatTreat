// ui/Resena/Resenauistate.kt
package com.example.beattreat.ui.Resena

data class ResenaUIState(
    val albumId: String = "",
    val albumNombre: String = "",
    val resenas: List<ResenaDetalladaUI> = emptyList(),
    val resenasLikeadas: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)