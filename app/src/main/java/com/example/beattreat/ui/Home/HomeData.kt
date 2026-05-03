package com.example.beattreat.ui.Home

// ── Datos locales de Home ──
// AHora si coinciden los IDs de front y back
// El orden de inserción en initAlbums.js es:
//   1 = A Night at the Opera (Queen)
//   2 = Un Verano Sin Ti (Bad Bunny)
//   3 = Midnights (Taylor Swift)
//   4 = News of the World (Queen)
//   5 = Renaissance (Beyoncé)
object HomeData {

    val banners = listOf(
        BannerUI(
            id        = 1,
            texto     = "Tu mejor ritmo todos los días",
            imagenUrl = "https://www.image2url.com/r2/default/images/1776563404271-554b7630-35dd-413b-84e9-c0b02943dee2.jpg"
        )
    )

    val artistas = listOf(
        ArtistaHomeUI(
            id     = 1,
            nombre = "Queen",
            albumes = listOf(
                AlbumHomeUI(1, "A Night at the Opera", "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png"),
                AlbumHomeUI(4, "News of the World",    "https://upload.wikimedia.org/wikipedia/en/e/ea/Queen_News_Of_The_World.png")
            )
        ),
        ArtistaHomeUI(
            id     = 2,
            nombre = "Bad Bunny",
            albumes = listOf(
                AlbumHomeUI(2, "Un Verano Sin Ti", "https://upload.wikimedia.org/wikipedia/en/6/60/Bad_Bunny_-_Un_Verano_Sin_Ti.png")
            )
        ),
        ArtistaHomeUI(
            id     = 3,
            nombre = "Taylor Swift",
            albumes = listOf(
                AlbumHomeUI(3, "Midnights", "https://upload.wikimedia.org/wikipedia/en/9/9f/Midnights_-_Taylor_Swift.png")
            )
        ),
        ArtistaHomeUI(
            id     = 4,
            nombre = "Beyoncé",
            albumes = listOf(
                AlbumHomeUI(5, "Renaissance", "https://upload.wikimedia.org/wikipedia/en/a/ad/Beyonc%C3%A9_-_Renaissance.png")
            )
        )
    )
}