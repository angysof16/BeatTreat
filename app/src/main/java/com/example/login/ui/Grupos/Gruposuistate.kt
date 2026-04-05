package com.example.login.ui.Grupos

data class GruposUIState(
    val grupos: List<GrupoChatUI> = emptyList(),
    val fotoPerfilUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)