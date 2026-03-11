package com.example.login.Home

data class HomeUIState(
    val bannerActual: Int = 0,
    val artistas: List<ArtistaHomeUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)