package com.example.beattreat.ui.Home

data class HomeUIState(
    val bannerActual: Int = 0,
    val bannerUrl: String = "https://www.image2url.com/r2/default/images/1776563404271-554b7630-35dd-413b-84e9-c0b02943dee2.jpg",
    val artistas: List<ArtistaHomeUI> = emptyList(),
    val fotoPerfilUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)