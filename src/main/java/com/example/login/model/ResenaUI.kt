package com.example.login.model

// ── Entidades UI para Reseñas ──

data class ResenaDetalladaUI(
    val id: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoRes: Int,
    val albumNombre: String,
    val albumArtista: String,
    val albumRes: Int,
    val calificacion: Float,
    val texto: String,
    val likes: Int,
    val comentarios: Int,
    val fecha: String
)

data class ComentarioUI(
    val id: Int,
    val autorNombre: String,
    val autorUsuario: String,
    val autorFotoRes: Int,
    val texto: String,
    val likes: Int,
    val fecha: String
)

// ── Datos locales quemados de Reseñas ──
object ResenaData {

    val resenasDestacadas = listOf(
        ResenaDetalladaUI(
            id = 1,
            autorNombre = "Alex Morrison",
            autorUsuario = "@alexmrrsn",
            autorFotoRes = 0, // R.drawable.foto_perfil
            albumNombre = "Nevermind",
            albumArtista = "Nirvana",
            albumRes = 0, // R.drawable.album_nevermind
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
            albumRes = 0,
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
            albumRes = 0,
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