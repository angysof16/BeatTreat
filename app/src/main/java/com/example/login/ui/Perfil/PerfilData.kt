package com.example.login.ui.Perfil

import com.example.login.R

// ── Datos locales de Perfil ──
// fotoPerfilUrl y fotoBannerUrl son Strings vacíos por defecto.
// Se actualizarán con la URL real de Firebase Storage cuando el usuario
// suba o cambie su foto.
object PerfilData {

    var perfilActual = PerfilUI(
        id            = 1,
        nombre        = "Alex Morrison",
        usuario       = "@alexmrrsn",
        fotoPerfilUrl = "",   // ← URL de Firebase Storage (vacío = placeholder)
        fotoBannerUrl = "",   // ← URL de Firebase Storage (vacío = placeholder)
        siguiendo     = 127,
        seguidores    = 89
    )

    val albumesFavoritos = listOf(
        AlbumPerfilUI(1, "Nevermind", R.drawable.album_mcr),
        AlbumPerfilUI(2, "In Utero",  R.drawable.album_nl),
        AlbumPerfilUI(3, "Pork Soda", R.drawable.album_ps)
    )

    val resenasRecientes = listOf(
        ResenaUI(
            id           = 1,
            autorNombre  = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoRes = 0,
            texto        = "One of the best CDs I own",
            likes        = 2,
            comentarios  = 2
        ),
        ResenaUI(
            id           = 2,
            autorNombre  = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoRes = 0,
            texto        = "Un álbum que definitivamente marcó una era en el rock alternativo",
            likes        = 5,
            comentarios  = 3
        )
    )
}