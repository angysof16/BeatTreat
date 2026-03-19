package com.example.login.ui.Perfil

// ── Entidades UI para Perfil ──

data class PerfilUI(
    val id: Int,
    val nombre: String,
    val usuario: String,
    val fotoPerfilUrl: String,   // ← antes era fotoPerfilRes: Int
    val fotoBannerUrl: String,   // ← antes era fotoBannerRes: Int
    val siguiendo: Int,
    val seguidores: Int
)

data class AlbumPerfilUI(
    val id: Int,
    val nombre: String,
    val imagenRes: Int           // Los álbumes siguen siendo locales
)

data class ResenaUI(
    val id: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoRes: Int,
    val texto: String,
    val likes: Int,
    val comentarios: Int
)