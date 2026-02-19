package com.example.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.login.screens.BibliotecaScreen
import com.example.login.screens.ChatScreen
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
                onSignUpClick = {
                    navController.navigate(Rutas.REGISTRO)
                },
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
                onSignInClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── 3. Home ──
        composable(Rutas.HOME) {
            HomeScreen(
                onAlbumClick = { albumId ->
                    // TODO: Navegar a detalle de álbum
                },
                onArtistaClick = { artistaId ->
                    // TODO: Navegar a detalle de artista
                },
                onSearchClick = {
                    // TODO: Navegar a búsqueda
                },
                onProfileClick = {
                    navController.navigate(Rutas.PERFIL)
                }
            )
        }

        // ── 4. Biblioteca ──
        composable(Rutas.BIBLIOTECA) {
            BibliotecaScreen(
                onPlaylistClick = { playlist ->
                    // TODO: Navegar a detalle de playlist
                },
                onCrearPlaylistClick = {
                    // TODO: Navegar a crear playlist
                }
            )
        }

        // ── 5. Descubre ──
        composable(Rutas.DESCUBRE) {
            DescubreScreen(
                onCategoriaClick = { categoria ->
                    // TODO: Navegar a categoría
                },
                onGeneroClick = { genero ->
                    // TODO: Navegar a género
                },
                onAlbumClick = { album ->
                    // TODO: Navegar a detalle de álbum
                },
                onSearchClick = {
                    // TODO: Navegar a búsqueda
                },
                onProfileClick = {
                    navController.navigate(Rutas.PERFIL)
                }
            )
        }

        // ── 6. Chat ──
        composable(Rutas.CHAT) {
            ChatScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── 7. Reseñas ──
        composable(Rutas.RESENA) {
            ResenaScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onResenaClick = { resena ->
                    // TODO: Navegar a detalle de reseña con comentarios
                },
                onEscribirResenaClick = {
                    navController.navigate(Rutas.ESCRIBIR_RESENA)
                }
            )
        }

        // ── 8. Escribir Reseña ──
        composable(Rutas.ESCRIBIR_RESENA) {
            EscribirResenaScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPublicarClick = { texto, calificacion ->
                    // TODO: Guardar reseña en base de datos
                    navController.popBackStack()
                }
            )
        }

        // ── 9. Perfil ──
        composable(Rutas.PERFIL) {
            ProfileScreen(
                onSearchClick = {
                    // TODO: Navegar a búsqueda
                },
                onEditProfileClick = {
                    // TODO: Navegar a editar perfil
                },
                onSiguiendoClick = {
                    // TODO: Ver lista de siguiendo
                },
                onMessageClick = {
                    navController.navigate(Rutas.CHAT)
                },
                onAlbumClick = { albumId ->
                    // TODO: Navegar a detalle de álbum
                },
                onVerTodasResenasClick = {
                    navController.navigate(Rutas.RESENA)
                }
            )
        }
    }
}