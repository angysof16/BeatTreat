package com.example.login.ui.PerfilOtroUsuario

/**
 * Datos del perfil de otro usuario tal como los muestra la pantalla.
 */
data class OtroUsuarioUI(
    val id: Int,
    val nombre: String,
    val username: String,   // "@alexmrrsn"
    val bio: String,
    val fotoPerfilUrl: String
)

/**
 * Representación visual de un review hecho por ese usuario.
 */
data class ReviewOtroUsuarioUI(
    val id: Int,
    val albumNombre: String,
    val albumArtista: String,
    val rating: Float,
    val contenido: String,
    val fecha: String
)

/**
 * Estado de la pantalla de perfil de otro usuario.
 */
data class PerfilOtroUsuarioUIState(
    val usuario: OtroUsuarioUI? = null,
    val reviews: List<ReviewOtroUsuarioUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
