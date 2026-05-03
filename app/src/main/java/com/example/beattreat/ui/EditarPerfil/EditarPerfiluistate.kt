package com.example.beattreat.ui.EditarPerfil

data class EditarPerfilUIState(
    val nombre: String           = "",
    val usuario: String          = "",
    val bio: String              = "",
    val fotoPerfilUrl: String    = "",   // URL actual o recién subida
    val isUploadingPhoto: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val isLoading: Boolean       = false,
    val errorMessage: String?    = null
)