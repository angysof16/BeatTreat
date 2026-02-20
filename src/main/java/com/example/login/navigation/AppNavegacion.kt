package com.example.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login.screens.BibliotecaScreen
import com.example.login.screens.ChatScreen
import com.example.login.screens.ComentariosScreen
import com.example.login.screens.DescubreScreen
import com.example.login.screens.EscribirResenaScreen
import com.example.login.screens.HomeScreen
import com.example.login.screens.LoginScreen
import com.example.login.screens.ProfileScreen
import com.example.login.screens.RegistroScreen
import com.example.login.screens.ResenaScreen

// ── Rutas de navegación ──
object Rutas {
    const val LOGIN           = "login"
    const val REGISTRO        = "registro"
    const val HOME            = "home"
    const val BIBLIOTECA      = "biblioteca"
    const val DESCUBRE        = "descubre"
    const val CHAT            = "chat"
    const val RESENA          = "resena"
    const val ESCRIBIR_RESENA = "escribir_resena"
    const val PERFIL          = "perfil"
    // ── Nueva ruta con argumento ──
    const val COMENTARIOS     = "comentarios/{resenaId}"

    // Helper para construir la ruta con el id real
    fun comentarios(resenaId: Int) = "comentarios/$resenaId"
}

@Composable
fun AppNavegacion(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        modifier = modifier
    ) {

        // ── 1. Login ──
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(Rutas.REGISTRO) },
                onForgotPasswordClick = { }
            )
        }

        // ── 2. Registro ──
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onGoogleClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onSignInClick = { navController.popBackStack() }
            )
        }

        // ── 3. Home ──
        composable(Rutas.HOME) {
            HomeScreen(
                onAlbumClick = {
                    // Un álbum lleva a ver sus reseñas
                    navController.navigate(Rutas.RESENA)
                },
                onArtistaClick = { /* TODO: detalle artista */ },
                onSearchClick  = { /* TODO: búsqueda */ },
                onProfileClick = { navController.navigate(Rutas.PERFIL) }
            )
        }

        // ── 4. Biblioteca ──
        composable(Rutas.BIBLIOTECA) {
            BibliotecaScreen(
                onPlaylistClick      = { /* TODO: detalle playlist */ },
                onCrearPlaylistClick = { /* TODO: crear playlist */ }
            )
        }

        // ── 5. Descubre ──
        composable(Rutas.DESCUBRE) {
            DescubreScreen(
                onCategoriaClick = { /* TODO: categoría */ },
                onGeneroClick    = { /* TODO: género */ },
                onAlbumClick     = { navController.navigate(Rutas.RESENA) },
                onSearchClick    = { /* TODO: búsqueda */ },
                onProfileClick   = { navController.navigate(Rutas.PERFIL) }
            )
        }

        // ── 6. Chat ──
        composable(Rutas.CHAT) {
            ChatScreen(onBackClick = { navController.popBackStack() })
        }

        // ── 7. Reseñas ──
        // Entrada 1 (desde Home) y entrada 2 (desde Descubre) llegan aquí
        composable(Rutas.RESENA) {
            ResenaScreen(
                onBackClick = { navController.popBackStack() },
                onResenaClick = { resena ->
                    navController.navigate(Rutas.comentarios(resena.id))
                },
                onEscribirResenaClick = { navController.navigate(Rutas.ESCRIBIR_RESENA) }
            )
        }

        // ── 8. Escribir Reseña ──
        composable(Rutas.ESCRIBIR_RESENA) {
            EscribirResenaScreen(
                onBackClick = { navController.popBackStack() },
                onPublicarClick = { _, _ ->
                    // TODO: guardar en base de datos
                    navController.popBackStack()
                }
            )
        }

        // ── 9. Perfil ──
        composable(Rutas.PERFIL) {
            ProfileScreen(
                onSearchClick          = { /* TODO */ },
                onEditProfileClick     = { /* TODO */ },
                onSiguiendoClick       = { /* TODO */ },
                onMessageClick         = { navController.navigate(Rutas.CHAT) },
                onAlbumClick           = { navController.navigate(Rutas.RESENA) },
                onVerTodasResenasClick = { navController.navigate(Rutas.RESENA) },
                onResenaClick = { resena ->
                    navController.navigate(Rutas.comentarios(resena.id))
                }
            )
        }

        // ── 10. Comentarios ──
        composable(
            route = Rutas.COMENTARIOS,
            arguments = listOf(
                navArgument("resenaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val resenaId = backStackEntry.arguments?.getInt("resenaId") ?: 0
            ComentariosScreen(
                resenaId  = resenaId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}