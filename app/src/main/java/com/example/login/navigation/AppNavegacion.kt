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
import com.example.login.screens.GruposScreen
import com.example.login.screens.HomeScreen
import com.example.login.screens.LoginScreen
import com.example.login.screens.ProfileScreen
import com.example.login.screens.RegistroScreen
import com.example.login.screens.ResenaScreen

@Composable
fun AppNavegacion(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {

        // ── 1. Login ──
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.Registro.route)
                },
                onForgotPasswordClick = { }
            )
        }

        // ── 2. Registro ──
        composable(route = Screen.Registro.route) {
            RegistroScreen(
                onRegistroClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoogleClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignInClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── 3. Home ──
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAlbumClick = {
                    navController.navigate(Screen.Resena.route)
                },
                onArtistaClick = { },
                onSearchClick = { },
                onProfileClick = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        // ── 4. Biblioteca ──
        composable(route = Screen.Biblioteca.route) {
            BibliotecaScreen(
                onPlaylistClick = { },
                onCrearPlaylistClick = { }
            )
        }

        // ── 5. Descubre ──
        composable(route = Screen.Descubre.route) {
            DescubreScreen(
                onCategoriaClick = { },
                onGeneroClick = { },
                onAlbumClick = {
                    navController.navigate(Screen.Resena.route)
                },
                onSearchClick = { },
                onProfileClick = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        // ── 6. Chat ──
        composable(route = Screen.Chat.route) {
            ChatScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── 7. Reseñas ──
        composable(route = Screen.Resena.route) {
            ResenaScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onResenaClick = { resena ->
                    navController.navigate(Screen.Comentarios.createRoute(resena.id))
                },
                onEscribirResenaClick = {
                    navController.navigate(Screen.EscribirResena.route)
                }
            )
        }

        // ── 8. Escribir Reseña ──
        composable(route = Screen.EscribirResena.route) {
            EscribirResenaScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPublicarClick = { _, _ ->
                    // TODO: guardar en base de datos
                    navController.popBackStack()
                }
            )
        }

        // ── 9. Perfil ──
        composable(route = Screen.Perfil.route) {
            ProfileScreen(
                onSearchClick = { },
                onEditProfileClick = { },
                onSiguiendoClick = { },
                onMessageClick = {
                    navController.navigate(Screen.Chat.route)
                },
                onAlbumClick = {
                    navController.navigate(Screen.Resena.route)
                },
                onVerTodasResenasClick = {
                    navController.navigate(Screen.Resena.route)
                },
                onResenaClick = { resena ->
                    navController.navigate(Screen.Comentarios.createRoute(resena.id))
                }
            )
        }

        // ── 10. Comentarios (con argumento) ──
        composable(
            route = Screen.Comentarios.route,
            arguments = listOf(
                navArgument("resenaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val resenaId = backStackEntry.arguments?.getInt("resenaId") ?: 0
            ComentariosScreen(
                resenaId = resenaId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── 11. Grupos ──
        composable(Screen.Grupos.route) {
            GruposScreen(
                onGrupoClick = {
                    navController.navigate(Screen.Chat.route)
                }
            )
        }
    }
}