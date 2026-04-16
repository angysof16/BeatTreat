package com.example.login.ui.Biblioteca

object BibliotecaData {

    val cancionesGuardadas = CancionGuardadaUI(
        id        = 1,
        titulo    = "Canciones guardadas",
        cantidad  = 157,
        imagenUrl = "HTTPS://PLACEHOLDER.COM/BIBLIOTECA/CANCIONES_GUARDADAS.JPG"
    )

    val artistas = ArtistaUI(
        id        = 1,
        nombre    = "Artistas",
        cantidad  = 25,
        imagenUrl = "HTTPS://PLACEHOLDER.COM/BIBLIOTECA/ARTISTAS.JPG"
    )

    val albumes = AlbumUI(
        id        = 1,
        titulo    = "Álbumes",
        cantidad  = 3,
        imagenUrl = "HTTPS://PLACEHOLDER.COM/BIBLIOTECA/ALBUMES.JPG"
    )

    val playlists = listOf(
        PlaylistUI(
            id          = 1,
            nombre      = "Álbumes",
            descripcion = "3 álbumes",
            imagenUrl   = "HTTPS://PLACEHOLDER.COM/PLAYLISTS/PLAYLIST_ALBUMES.JPG"
        ),
        PlaylistUI(
            id          = 2,
            nombre      = "Música Lo-Fi",
            descripcion = "Música Chill",
            imagenUrl   = "HTTPS://PLACEHOLDER.COM/PLAYLISTS/PLAYLIST_LOFI.JPG"
        )
    )
}