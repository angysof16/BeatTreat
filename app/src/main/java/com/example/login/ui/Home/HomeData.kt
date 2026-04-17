package com.example.login.ui.Home

// ── Datos locales de Home ──
object HomeData {

    val banners = listOf(
        BannerUI(
            id        = 1,
            texto     = "Tu mejor ritmo todos los días",
            imagenUrl = "https://cdn.phototourl.com/free/2026-04-16-9dcb5791-faff-46af-93c2-6f2f7b9fea48.jpg"
        )
    )

    val artistas = listOf(
        ArtistaHomeUI(
            id     = 1,
            nombre = "Quiet Riot",
            albumes = listOf(
                AlbumHomeUI(1, "Cum on Feel the Noize", "https://cdn.phototourl.com/free/2026-04-16-9c152d81-5c35-47ec-a295-aa26549c1c38.png"),
                AlbumHomeUI(2, "Mama Weer All Crazee Now", "https://cdn.phototourl.com/free/2026-04-16-12a209bd-bb13-4979-940b-bf7e38d3bb73.jpg"),
                AlbumHomeUI(3, "The Wild The You", "https://cdn.phototourl.com/free/2026-04-16-9fcaa3c4-af8d-4ceb-b45e-f93ec944b474.jpg")
            )
        ),
        ArtistaHomeUI(
            id     = 2,
            nombre = "Bad Bunny",
            albumes = listOf(
                AlbumHomeUI(4, "X100 PRE", "https://cdn.phototourl.com/free/2026-04-16-40786062-8389-4199-bf77-c95e86398801.webp"),
                AlbumHomeUI(5, "Un Verano Sin Ti", "https://cdn.phototourl.com/free/2026-04-16-f5b9a8aa-ad44-4c97-8521-3752902c1411.webp"),
                AlbumHomeUI(6, "Las Que No Iban A Salir", "HTTPS://PLACEHOLDER.COM/ALBUMS/BAD_BUNNY_LQNIAS.JPG")
            )
        ),
        ArtistaHomeUI(
            id     = 3,
            nombre = "Queen",
            albumes = listOf(
                AlbumHomeUI(7, "A Night at the Opera", "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png"),
                AlbumHomeUI(8, "The Game", "https://cdn.phototourl.com/free/2026-04-16-7eb3fce1-c3ac-4657-b20b-56145f2845de.png"),
                AlbumHomeUI(9, "News of the World", "https://cdn.phototourl.com/free/2026-04-16-7eb3fce1-c3ac-4657-b20b-56145f2845de.png")
            )
        )
    )
}