package com.example.login.ui.Perfil

data class ProfileUIState(
    val perfil: PerfilUI? = null,
    val albumesFavoritos: List<AlbumPerfilUI> = emptyList(),
    // resenas ahora viene del backend (vacío por defecto, sin hardcode)
    val resenas: List<ResenaUI> = emptyList(),
    val cerrarSesionExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val errorMessage: String? = null
) {
    val fotoPerfilUrl: String get() = perfil?.fotoPerfilUrl ?: ""
}