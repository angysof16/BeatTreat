package com.example.login.ui.Perfil

// ── Entidades UI para Perfil ──

data class PerfilUI(
    val id: Int,
    val nombre: String,
    val usuario: String,
    val fotoPerfilUrl: String,
    val fotoBannerUrl: String,
    val siguiendo: Int,
    val seguidores: Int
)

data class AlbumPerfilUI(
    val id: Int,
    val nombre: String,
    val imagenUrl: String
)

data class ResenaUI(
    val id: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoUrl: String,
    val texto: String,
    val likes: Int,
    val comentarios: Int
)