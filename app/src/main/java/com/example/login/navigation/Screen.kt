package com.example.login.navigation

// ── Sealed class para encapsular todas las rutas ──
sealed class Screen(val route: String) {

    // ── Pantallas sin argumentos ──
    object Login          : Screen("login")
    object Registro       : Screen("registro")
    object Home           : Screen("home")
    object Biblioteca     : Screen("biblioteca")
    object Descubre       : Screen("descubre")
    object EscribirResena : Screen("escribir_resena")
    object Perfil         : Screen("perfil")
    object Grupos         : Screen("grupos")
    object Chat           : Screen("chat")

    // ── Pantallas con argumentos ──

    // Reseñas filtradas por álbum
    object Resena : Screen("resena/{albumId}") {
        fun createRoute(albumId: Int) = "resena/$albumId"
    }

    // Comentarios de una reseña específica
    object Comentarios : Screen("comentarios/{resenaId}") {
        fun createRoute(resenaId: Int) = "comentarios/$resenaId"
    }

    // Detalle de un álbum específico
    // Los álbumes de HomeData usan IDs 1-9
    // Los álbumes de DescubreData usan ID original + 100 para evitar colisiones
    object AlbumDetalle : Screen("album_detalle/{albumId}") {
        fun createRoute(albumId: Int) = "album_detalle/$albumId"
    }
}