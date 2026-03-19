package com.example.login.ui.Grupos

import com.example.login.ui.Grupos.GrupoChatUI

data class GruposUIState(
    val grupos: List<GrupoChatUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)