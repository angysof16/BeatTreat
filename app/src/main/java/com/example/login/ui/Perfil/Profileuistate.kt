package com.example.login.ui.Perfil

data class ProfileUIState(
    val perfil: PerfilUI? = null,
    val albumesFavoritos: List<AlbumPerfilUI> = emptyList(),
    val resenas: List<ResenaUI> = emptyList(),
    val cerrarSesionExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)