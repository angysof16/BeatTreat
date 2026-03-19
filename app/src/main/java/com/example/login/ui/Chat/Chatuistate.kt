package com.example.login.ui.Chat

import com.example.login.ui.Chat.MensajeUI

data class ChatUIState(
    val mensajes: List<MensajeUI> = emptyList(),
    val mensajeTexto: String = "",
    val nombreGrupo: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
