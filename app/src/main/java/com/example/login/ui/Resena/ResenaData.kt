package com.example.login.ui.Resena

object ResenaData {

    val todasLasResenas = listOf(
        ResenaDetalladaUI(
            id            = 1,
            albumId       = 7,
            autorNombre   = "Alex Morrison",
            autorUsuario  = "@alexmrrsn",
            autorFotoUrl  = "HTTPS://PLACEHOLDER.COM/USERS/ALEX_MORRISON_AVATAR.JPG",
            albumNombre   = "A Night at the Opera",
            albumArtista  = "Queen",
            albumImagenUrl = "HTTPS://PLACEHOLDER.COM/ALBUMS/QUEEN_NIGHT_OPERA.JPG",
            calificacion  = 4.5f,
            texto         = "One of the best CDs I own. Un álbum que revolucionó el rock alternativo y definió una generación.",
            likes         = 24,
            comentarios   = 8,
            fecha         = "Hace 2 días"
        ),
        ResenaDetalladaUI(
            id            = 2,
            albumId       = 5,
            autorNombre   = "María García",
            autorUsuario  = "@mariagrck",
            autorFotoUrl  = "HTTPS://PLACEHOLDER.COM/USERS/MARIA_GARCIA_AVATAR.JPG",
            albumNombre   = "Un Verano Sin Ti",
            albumArtista  = "Bad Bunny",
            albumImagenUrl = "HTTPS://PLACEHOLDER.COM/ALBUMS/BAD_BUNNY_UVST.JPG",
            calificacion  = 5.0f,
            texto         = "El mejor álbum del año sin duda. Bad Bunny superó todas las expectativas.",
            likes         = 156,
            comentarios   = 42,
            fecha         = "Hace 5 días"
        ),
        ResenaDetalladaUI(
            id            = 3,
            albumId       = 103,
            autorNombre   = "Carlos Ruiz",
            autorUsuario  = "@carlosrz",
            autorFotoUrl  = "HTTPS://PLACEHOLDER.COM/USERS/CARLOS_RUIZ_AVATAR.JPG",
            albumNombre   = "Un Verano Sin Ti",
            albumArtista  = "Bad Bunny",
            albumImagenUrl = "HTTPS://PLACEHOLDER.COM/ALBUMS/BAD_BUNNY_UVST.JPG",
            calificacion  = 5.0f,
            texto         = "Una obra maestra atemporal. Cada vez que lo escucho descubro algo nuevo.",
            likes         = 89,
            comentarios   = 23,
            fecha         = "Hace 1 semana"
        ),
        ResenaDetalladaUI(
            id            = 4,
            albumId       = 101,
            autorNombre   = "Laura Pérez",
            autorUsuario  = "@laurapz",
            autorFotoUrl  = "HTTPS://PLACEHOLDER.COM/USERS/LAURA_PEREZ_AVATAR.JPG",
            albumNombre   = "Midnights",
            albumArtista  = "Taylor Swift",
            albumImagenUrl = "HTTPS://PLACEHOLDER.COM/ALBUMS/TAYLOR_SWIFT_MIDNIGHTS.JPG",
            calificacion  = 4.7f,
            texto         = "Un álbum íntimo y oscuro que te atrapa desde la primera canción.",
            likes         = 210,
            comentarios   = 55,
            fecha         = "Hace 3 días"
        ),
        ResenaDetalladaUI(
            id            = 5,
            albumId       = 102,
            autorNombre   = "Diego Torres",
            autorUsuario  = "@diegot",
            autorFotoUrl  = "HTTPS://PLACEHOLDER.COM/USERS/DIEGO_TORRES_AVATAR.JPG",
            albumNombre   = "Renaissance",
            albumArtista  = "Beyoncé",
            albumImagenUrl = "HTTPS://PLACEHOLDER.COM/ALBUMS/BEYONCE_RENAISSANCE.JPG",
            calificacion  = 4.8f,
            texto         = "Beyoncé creó un tributo perfecto a la música dance y house.",
            likes         = 320,
            comentarios   = 78,
            fecha         = "Hace 4 días"
        )
    )

    fun porAlbum(albumId: Int): List<ResenaDetalladaUI> =
        todasLasResenas.filter { it.albumId == albumId }

    val comentariosEjemplo = listOf(
        ComentarioUI(
            id           = 1,
            autorNombre  = "John Doe",
            autorUsuario = "@johndoe",
            autorFotoUrl = "https://cdn.phototourl.com/free/2026-04-16-45a5ba1b-ce39-4e85-a6b8-36010bd95f71.png",
            texto        = "Totalmente de acuerdo, este álbum es increíble!",
            likes        = 5,
            fecha        = "Hace 1 día"
        ),
        ComentarioUI(
            id           = 2,
            autorNombre  = "Jane Smith",
            autorUsuario = "@janesmith",
            autorFotoUrl = "https://cdn.phototourl.com/free/2026-04-16-45a5ba1b-ce39-4e85-a6b8-36010bd95f71.png",
            texto        = "La primera canción es mi favorita del álbum",
            likes        = 3,
            fecha        = "Hace 3 horas"
        )
    )
}