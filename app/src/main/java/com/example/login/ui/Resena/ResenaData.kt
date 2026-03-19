package com.example.login.ui.Resena

import com.example.login.R

object ResenaData {

    // Todas las reseñas de la app. albumId referencia el id de AlbumDetalleData.
    val todasLasResenas = listOf(
        ResenaDetalladaUI(
            id = 1,
            albumId = 7,          // A Night at the Opera - Queen
            autorNombre = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoRes = R.drawable.foto_perfil,
            albumNombre = "A Night at the Opera",
            albumArtista = "Queen",
            albumRes = 0,
            calificacion = 4.5f,
            texto = "One of the best CDs I own. Un álbum que revolucionó el rock alternativo y definió una generación. La producción es impecable y cada canción es memorable.",
            likes = 24,
            comentarios = 8,
            fecha = "Hace 2 días"
        ),
        ResenaDetalladaUI(
            id = 2,
            albumId = 5,          // Un Verano Sin Ti - Bad Bunny
            autorNombre = "María García",
            autorUsuario = "@mariagrck",
            autorFotoRes = 0,
            albumNombre = "Un Verano Sin Ti",
            albumArtista = "Bad Bunny",
            albumRes = R.drawable.album5,
            calificacion = 5.0f,
            texto = "El mejor álbum del año sin duda. Bad Bunny superó todas las expectativas con este trabajo. Perfecto para el verano.",
            likes = 156,
            comentarios = 42,
            fecha = "Hace 5 días"
        ),
        ResenaDetalladaUI(
            id = 3,
            albumId = 103,        // Un Verano Sin Ti en Descubre (offset +100)
            autorNombre = "Carlos Ruiz",
            autorUsuario = "@carlosrz",
            autorFotoRes = 0,
            albumNombre = "Un Verano Sin Ti",
            albumArtista = "Bad Bunny",
            albumRes = R.drawable.album5,
            calificacion = 5.0f,
            texto = "Una obra maestra atemporal. Cada vez que lo escucho descubro algo nuevo. La experiencia sonora es simplemente perfecta.",
            likes = 89,
            comentarios = 23,
            fecha = "Hace 1 semana"
        ),
        ResenaDetalladaUI(
            id = 4,
            albumId = 101,        // Midnights - Taylor Swift
            autorNombre = "Laura Pérez",
            autorUsuario = "@laurapz",
            autorFotoRes = 0,
            albumNombre = "Midnights",
            albumArtista = "Taylor Swift",
            albumRes = R.drawable.album_midnights,
            calificacion = 4.7f,
            texto = "Un álbum íntimo y oscuro que te atrapa desde la primera canción. Anti-Hero es un himno generacional.",
            likes = 210,
            comentarios = 55,
            fecha = "Hace 3 días"
        ),
        ResenaDetalladaUI(
            id = 5,
            albumId = 102,        // Renaissance - Beyoncé
            autorNombre = "Diego Torres",
            autorUsuario = "@diegot",
            autorFotoRes = 0,
            albumNombre = "Renaissance",
            albumArtista = "Beyoncé",
            albumRes = R.drawable.album_renaissance,
            calificacion = 4.8f,
            texto = "Beyoncé creó un tributo perfecto a la música dance y house. Break My Soul es uno de los mejores temas del año.",
            likes = 320,
            comentarios = 78,
            fecha = "Hace 4 días"
        )
    )

    // Filtra las reseñas que corresponden a un álbum específico
    fun porAlbum(albumId: Int): List<ResenaDetalladaUI> =
        todasLasResenas.filter { it.albumId == albumId }

    val comentariosEjemplo = listOf(
        ComentarioUI(
            id = 1,
            autorNombre = "John Doe",
            autorUsuario = "@johndoe",
            autorFotoRes = 0,
            texto = "Totalmente de acuerdo, este álbum es increíble!",
            likes = 5,
            fecha = "Hace 1 día"
        ),
        ComentarioUI(
            id = 2,
            autorNombre = "Jane Smith",
            autorUsuario = "@janesmith",
            autorFotoRes = 0,
            texto = "La primera canción es mi favorita del álbum",
            likes = 3,
            fecha = "Hace 3 horas"
        )
    )
}