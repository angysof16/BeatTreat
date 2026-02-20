package com.example.login.model

import com.example.login.R

object ResenaData {

    val resenasDestacadas = listOf(
        ResenaDetalladaUI(
            id = 1,
            autorNombre = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoRes = R.drawable.foto_perfil,
            albumNombre = "Nevermind",
            albumArtista = "Nirvana",
            albumRes = R.drawable.album_nevermind,
            calificacion = 4.5f,
            texto = "One of the best CDs I own. Un álbum que revolucionó el rock alternativo y definió una generación. La producción es impecable y cada canción es memorable.",
            likes = 24,
            comentarios = 8,
            fecha = "Hace 2 días"
        ),
        ResenaDetalladaUI(
            id = 2,
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
            autorNombre = "Carlos Ruiz",
            autorUsuario = "@carlosrz",
            autorFotoRes = 0,
            albumNombre = "The Dark Side of the Moon",
            albumArtista = "Pink Floyd",
            // ✅ Descargar: busca "Dark Side of the Moon Pink Floyd album cover" → guardar como album_dark_side
            albumRes = R.drawable.album_dark_side,
            calificacion = 5.0f,
            texto = "Una obra maestra atemporal. Cada vez que lo escucho descubro algo nuevo. La experiencia sonora es simplemente perfecta.",
            likes = 89,
            comentarios = 23,
            fecha = "Hace 1 semana"
        )
    )

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
            texto = "Smells Like Teen Spirit es mi favorita del álbum",
            likes = 3,
            fecha = "Hace 3 horas"
        )
    )
}