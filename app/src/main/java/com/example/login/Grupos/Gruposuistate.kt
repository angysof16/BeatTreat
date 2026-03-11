package com.example.login.Grupos

import com.example.login.Grupos.GrupoChatUI

data class GruposUIState(
    val grupos: List<GrupoChatUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)