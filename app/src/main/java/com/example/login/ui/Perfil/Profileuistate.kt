package com.example.login.ui.Perfil

data class ProfileUIState(
    val perfil: PerfilUI? = null,
    val albumesFavoritos: List<AlbumPerfilUI> = emptyList(),
    // Legacy simple reviews (kept for backward compat but unused in UI now)
    val resenas: List<ResenaUI> = emptyList(),
    // New: reviews with full album info for MiPerfil-style display
    val resenasConAlbum: List<ResenaConAlbumUI> = emptyList(),
    val cerrarSesionExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val errorMessage: String? = null
) {
    val fotoPerfilUrl: String get() = perfil?.fotoPerfilUrl ?: ""
}

data class ResenaConAlbumUI(
    val id: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoUrl: String,
    val texto: String,
    val likes: Int,
    val comentarios: Int,
    val albumNombre: String,
    val albumArtista: String,
    val albumCover: String,
    val calificacion: Float
)