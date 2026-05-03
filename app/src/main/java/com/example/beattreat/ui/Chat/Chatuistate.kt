package com.example.beattreat.ui.Chat

data class ChatUIState(
    val mensajes: List<MensajeUI> = emptyList(),
    val mensajeTexto: String = "",
    val nombreGrupo: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
