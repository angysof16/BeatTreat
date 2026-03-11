package com.example.login.Perfil

data class ProfileUIState(
    val perfil: PerfilUI? = null,
    val albumesFavoritos: List<AlbumPerfilUI> = emptyList(),
    val resenas: List<ResenaUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
