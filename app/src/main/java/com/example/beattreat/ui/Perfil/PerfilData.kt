package com.example.beattreat.ui.Perfil

// ── Datos locales de Perfil ──
// fotoPerfilUrl y fotoBannerUrl se actualizan desde Firebase Storage
// albumesFavoritos usan URLs placeholder hasta tenerlas reales
object PerfilData {

    var perfilActual = PerfilUI(
        id            = 1,
        nombre        = "Alex Morrison",
        usuario       = "@alexmrrsn",
        // se actualizan con firebase storage
        fotoPerfilUrl = "",
        fotoBannerUrl = "",
        siguiendo     = 127,
        seguidores    = 89,
        bio = ""
    )

    val albumesFavoritos = listOf(
        AlbumPerfilUI(1, "Nevermind",  "HTTPS://PLACEHOLDER.COM/ALBUMS/NIRVANA_NEVERMIND.JPG"),
        AlbumPerfilUI(2, "In Utero",   "HTTPS://PLACEHOLDER.COM/ALBUMS/NIRVANA_IN_UTERO.JPG"),
        AlbumPerfilUI(3, "Pork Soda",  "HTTPS://PLACEHOLDER.COM/ALBUMS/PRIMUS_PORK_SODA.JPG")
    )

    val resenasRecientes = listOf(
        ResenaUI(
            id           = 1,
            autorNombre  = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoUrl = "",
            texto        = "One of the best CDs I own",
            likes        = 2,
            comentarios  = 2
        ),
        ResenaUI(
            id           = 2,
            autorNombre  = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoUrl = "",
            texto        = "Un álbum que definitivamente marcó una era en el rock alternativo",
            likes        = 5,
            comentarios  = 3
        )
    )
}