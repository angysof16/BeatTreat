package com.example.login.model

import com.example.login.R

// ── Entidades UI para Perfil ──

data class PerfilUI(
    val id: Int,
    val nombre: String,
    val usuario: String,
    val fotoPerfilRes: Int,
    val fotoBannerRes: Int,
    val siguiendo: Int,
    val seguidores: Int
)

data class AlbumPerfilUI(
    val id: Int,
    val nombre: String,
    val imagenRes: Int
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
