package com.example.login.model

import com.example.login.R

// ── Datos locales quemados de Biblioteca ──
object BibliotecaData {

    val cancionesGuardadas = CancionGuardadaUI(
        id = 1,
        titulo = "Canciones guardadas",
        cantidad = 157,
        imagenRes = R.drawable.album6
    )

    val artistas = ArtistaUI(
        id = 1,
        nombre = "Artistas",
        cantidad = 25,
        imagenRes = R.drawable.westcol
    )

    val albumes = AlbumUI(
        id = 1,
        titulo = "Álbumes",
        cantidad = 3,
        imagenRes = R.drawable.albumgeneral
    )

    val playlists = listOf(
        PlaylistUI(
            id = 1,
            nombre = "Álbumes",
            descripcion = "3 álbumes",
            imagenRes = R.drawable.albumgeneral
        ),
        PlaylistUI(
            id = 2,
            nombre = "Música Lo-Fi",
            descripcion = "Música Chill",
            imagenRes =  R.drawable.playlist_lofi
        )
    )
}