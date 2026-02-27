package com.example.login.model

import com.example.login.R

// ── Datos locales quemados de Perfil ──
object PerfilData {

    val perfilActual = PerfilUI(
        id = 1,
        nombre = "Alex Morrison",
        usuario = "@alexmrrsn",
        fotoPerfilRes = R.drawable.foto_perfil,
        fotoBannerRes = R.drawable.banner_perfil,
        siguiendo = 127,
        seguidores = 89
    )

    val albumesFavoritos = listOf(
        AlbumPerfilUI(1, "Nevermind", R.drawable.album_mcr), // R.drawable.album_nv
        AlbumPerfilUI(2, "In Utero", R.drawable.album_nl), // R.drawable.album_iu
        AlbumPerfilUI(3, "Pork Soda", R.drawable.album_ps) // R.drawable.album_ps
    )

    val resenasRecientes = listOf(
        ResenaUI(
            id = 1,
            autorNombre = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoRes = R.drawable.foto_perfil, // R.drawable.foto_perfil
            texto = "One of the best CDs I own",
            likes = 2,
            comentarios = 2
        ),
        ResenaUI(
            id = 2,
            autorNombre = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoRes = R.drawable.foto_perfil,
            texto = "Un álbum que definitivamente marcó una era en el rock alternativo",
            likes = 5,
            comentarios = 3,
        )
    )
}
