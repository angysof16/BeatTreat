package com.example.login.model

import com.example.login.R


// ── Datos locales quemados de Home ──
object HomeData {

    val banners = listOf(
        BannerUI(
            id = 1,
            texto = "Tu mejor ritmo todos los días",
            imagenRes = R.drawable.banner // R.drawable.banner
        )
    )

    val artistas = listOf(
        ArtistaHomeUI(
            id = 1,
            nombre = "Quiet Riot",
            albumes = listOf(
                AlbumHomeUI(1, "Cum on Feel the Noize", R.drawable.album1), // R.drawable.album1
                AlbumHomeUI(2, "Mama Weer All Crazee Now", R.drawable.album2), // R.drawable.album2
                AlbumHomeUI(3, "The Wild The You", R.drawable.album3) // R.drawable.album3
            )
        ),
        ArtistaHomeUI(
            id = 2,
            nombre = "Bad Bunny",
            albumes = listOf(
                AlbumHomeUI(4, "X100 PRE", R.drawable.album4), // R.drawable.album4
                AlbumHomeUI(5, "Un Verano Sin Ti", R.drawable.album5), // R.drawable.album5
                AlbumHomeUI(6, "Las Que No Iban A Salir", R.drawable.album6) // R.drawable.album6
            )
        ),
        ArtistaHomeUI(
            id = 3,
            nombre = "Queen",
            albumes = listOf(
                AlbumHomeUI(7, "A Night at the Opera", 0),
                AlbumHomeUI(8, "The Game", 0),
                AlbumHomeUI(9, "News of the World", 0)
            )
        )
    )
}