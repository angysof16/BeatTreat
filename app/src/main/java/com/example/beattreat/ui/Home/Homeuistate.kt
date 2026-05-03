package com.example.beattreat.ui.Home

data class HomeUIState(
    val bannerActual: Int = 0,
    val bannerUrl: String = "https://www.image2url.com/r2/default/images/1776301063731-2da3393d-c59d-482f-96a2-27147f499b9d.jpg",
    val artistas: List<ArtistaHomeUI> = emptyList(),
    val fotoPerfilUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)