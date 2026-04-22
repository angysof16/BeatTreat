package com.example.login.ui.PerfilOtroUsuario

data class OtroUsuarioUI(
    val id: Int,
    val nombre: String,
    val username: String,
    val bio: String,
    val fotoPerfilUrl: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0
)

data class ReviewOtroUsuarioUI(
    val id: Int,
    val albumNombre: String,
    val albumArtista: String,
    val rating: Float,
    val contenido: String,
    val fecha: String
)

/**
 * FIX: se agrega isFollowLoading para deshabilitar el botón
 * mientras se espera la respuesta de Firestore.
 * Esto evita el doble-tap que causaba follow→unfollow inmediato.
 */
data class PerfilOtroUsuarioUIState(
    val usuario: OtroUsuarioUI? = null,
    val reviews: List<ReviewOtroUsuarioUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFollowing: Boolean = false,
    val puedeFollow: Boolean = false,
    // FIX Bug #2: deshabilita el botón mientras espera Firestore
    val isFollowLoading: Boolean = false
)
