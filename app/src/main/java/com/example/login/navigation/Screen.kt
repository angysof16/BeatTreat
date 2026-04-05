package com.example.login.navigation

sealed class Screen(val route: String) {

    // ── Pantallas sin argumentos ──────────────────────────────────────────────
    object Login          : Screen("login")
    object Registro       : Screen("registro")
    object Home           : Screen("home")
    object Biblioteca     : Screen("biblioteca")
    object Descubre       : Screen("descubre")
    object EscribirResena : Screen("escribir_resena")
    object Perfil         : Screen("perfil")
    object Grupos         : Screen("grupos")
    object Chat           : Screen("chat")
    object Buscar         : Screen("buscar")
    object CrearPlaylist  : Screen("crear_playlist")
    object EditarPerfil   : Screen("editar_perfil")

    // ── Pantallas con argumentos ──────────────────────────────────────────────

    object Resena : Screen("resena/{albumId}") {
        fun createRoute(albumId: Int) = "resena/$albumId"
    }

    object Comentarios : Screen("comentarios/{resenaId}") {
        fun createRoute(resenaId: Int) = "comentarios/$resenaId"
    }

    object AlbumDetalle : Screen("album_detalle/{albumId}") {
        fun createRoute(albumId: Int) = "album_detalle/$albumId"
    }

    object ArtistaDetalle : Screen("artista_detalle/{artistaId}") {
        fun createRoute(artistaId: Int) = "artista_detalle/$artistaId"
    }

    object GeneroDetalle : Screen("genero_detalle/{generoId}") {
        fun createRoute(generoId: Int) = "genero_detalle/$generoId"
    }

    object CategoriaDetalle : Screen("categoria_detalle/{categoriaId}") {
        fun createRoute(categoriaId: Int) = "categoria_detalle/$categoriaId"
    }

    object PlaylistDetalle : Screen("playlist_detalle/{playlistId}") {
        fun createRoute(playlistId: Int) = "playlist_detalle/$playlistId"
    }

    object Seguidores : Screen("seguidores/{tipo}") {
        fun createRoute(tipo: String) = "seguidores/$tipo"
    }

    object ResenasUsuario : Screen("resenas_usuario/{usuarioId}") {
        fun createRoute(usuarioId: Int) = "resenas_usuario/$usuarioId"
    }

    /**
     * Pantalla de perfil de OTRO usuario.
     * Recibe el userId del backend (Int).
     * Ejemplo: "perfil_usuario/2"
     */
    object PerfilOtroUsuario : Screen("perfil_usuario/{userId}") {
        fun createRoute(userId: Int) = "perfil_usuario/$userId"
    }
}