package com.example.login.ui.Home

data class HomeUIState(
    val bannerActual: Int = 0,
    val artistas: List<ArtistaHomeUI> = emptyList(),
    val fotoPerfilUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)