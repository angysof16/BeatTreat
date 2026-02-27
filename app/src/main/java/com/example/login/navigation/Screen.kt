package com.example.login.navigation

// ── Sealed class para encapsular las rutas ──
sealed class Screen(val route: String) {

    // Pantallas sin argumentos
    object Login : Screen("login")
    object Registro : Screen("registro")
    object Home : Screen("home")
    object Biblioteca : Screen("biblioteca")
    object Descubre : Screen("descubre")
    object Resena : Screen("resena")
    object EscribirResena : Screen("escribir_resena")
    object Perfil : Screen("perfil")

    // Pantalla con argumento (resenaId)
    object Comentarios : Screen("comentarios/{resenaId}") {
        fun createRoute(resenaId: Int) = "comentarios/$resenaId"
    }

    // Lista de grupos
    object Grupos : Screen("grupos")

    // Chat sin argumentos
    object Chat : Screen("chat")
}