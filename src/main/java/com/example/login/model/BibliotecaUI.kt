package com.example.login.model

import com.example.login.R

// ── Entidades UI para Biblioteca ──

data class CancionGuardadaUI(
    val id: Int,
    val titulo: String,
    val cantidad: Int,
    val imagenRes: Int
)

data class ArtistaUI(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val imagenRes: Int
)

data class AlbumUI(
    val id: Int,
    val titulo: String,
    val cantidad: Int,
    val imagenRes: Int
)

data class PlaylistUI(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagenRes: Int
)

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