package com.example.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login.screens.AlbumDetalleScreen

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

    // Comentarios de una reseña
    const val COMENTARIOS     = "comentarios/{resenaId}"
    fun comentarios(resenaId: Int) = "comentarios/$resenaId"

    // ── NUEVO: Detalle de álbum ──
    // Los álbumes de HomeData usan IDs 1-9.
    // Los álbumes de DescubreData usan IDs 1-5, pero se navega con offset +100
    // (ID real en AlbumDetalleData = id_descubre + 100).
    const val ALBUM_DETALLE   = "album_detalle/{albumId}"
    fun albumDetalle(albumId: Int) = "album_detalle/$albumId"
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
                // Cada álbum de HomeData tiene su propio id (1-9)
                onAlbumClick = { albumId ->
                    navController.navigate(Rutas.albumDetalle(albumId))
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
                // Los álbumes de DescubreData tienen IDs 1-5;
                // en AlbumDetalleData están guardados con ID = original + 100
                onAlbumClick = { album ->
                    navController.navigate(Rutas.albumDetalle(album.id + 100))
                },
                onSearchClick    = { /* TODO: búsqueda */ },
                onProfileClick   = { navController.navigate(Rutas.PERFIL) }
            )
        }

        // ── 6. Chat ──
        composable(Rutas.CHAT) {
            ChatScreen(onBackClick = { navController.popBackStack() })
        }

        // ── 7. Reseñas ──
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
                onAlbumClick           = { albumId ->
                    navController.navigate(Rutas.albumDetalle(albumId))
                },
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

        // ── 11. NUEVO: Detalle de Álbum ──
        composable(
            route = Rutas.ALBUM_DETALLE,
            arguments = listOf(
                navArgument("albumId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getInt("albumId") ?: 0
            AlbumDetalleScreen(
                albumId = albumId,
                onBackClick = { navController.popBackStack() },
                onVerResenasClick = { navController.navigate(Rutas.RESENA) }
            )
        }
    }
}