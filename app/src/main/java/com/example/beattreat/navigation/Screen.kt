// navigation/Screen.kt - versión completa corregida
package com.example.beattreat.navigation

sealed class Screen(val route: String) {

    // ── Pantallas sin argumentos ──────────────────────────────────────────────
    object Login         : Screen("login")
    object Registro      : Screen("registro")
    object Home          : Screen("home")
    object Biblioteca    : Screen("biblioteca")
    object Descubre      : Screen("descubre")
    object Perfil        : Screen("perfil")
    object Grupos        : Screen("grupos")
    object Chat          : Screen("chat")
    object Buscar        : Screen("buscar")
    object CrearPlaylist : Screen("crear_playlist")
    object EditarPerfil  : Screen("editar_perfil")
    object MiPerfil      : Screen("mi_perfil")
    object FeedSiguiendo : Screen("feed_siguiendo")

    // ── Pantallas con argumentos ──────────────────────────────────────────────

    object EscribirResena : Screen("escribir_resena/{albumId}") {
        fun createRoute(albumId: Int = 0) = "escribir_resena/$albumId"
    }

    object Resena : Screen("resena/{albumId}") {
        fun createRoute(albumId: String) = "resena/$albumId"  // ← Sobrecarga para String
        fun createRoute(albumId: Int) = "resena/$albumId"     // ← Sobrecarga para Int
    }

    object Comentarios : Screen("comentarios/{resenaId}/{albumId}") {
        fun createRoute(resenaId: String, albumId: String) = "comentarios/$resenaId/$albumId"
        fun createRoute(resenaId: String, albumId: Int) = "comentarios/$resenaId/$albumId"
        fun createRoute(resenaId: Int, albumId: Int) = "comentarios/$resenaId/$albumId"
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

    object PerfilOtroUsuario : Screen("perfil_usuario/{userId}") {
        fun createRoute(userId: String) = "perfil_usuario/$userId"
        fun createRoute(userId: Int) = "perfil_usuario/$userId"
    }
}