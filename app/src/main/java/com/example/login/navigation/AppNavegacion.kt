package com.example.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login.AlbumDetalle.AlbumDetalleScreen
import com.example.login.Biblioteca.BibliotecaScreen
import com.example.login.Chat.ChatScreen
import com.example.login.Comentarios.ComentariosScreen
import com.example.login.Descubre.DescubreScreen
import com.example.login.EscribirResena.EscribirResenaScreen
import com.example.login.Grupos.GruposScreen
import com.example.login.Home.HomeScreen
import com.example.login.Login.LoginScreen
import com.example.login.Login.LoginViewModel
import com.example.login.Perfil.ProfileScreen
import com.example.login.Registro.RegistroScreen
import com.example.login.Registro.RegistroViewModel
import com.example.login.Resena.ResenaScreen

import androidx.compose.runtime.getValue
import com.example.login.AlbumDetalle.AlbumDetalleViewModel
import com.example.login.Biblioteca.BibliotecaViewModel
import com.example.login.Chat.ChatViewModel
import com.example.login.Comentarios.ComentariosViewModel
import com.example.login.Descubre.DescubreViewModel
import com.example.login.EscribirResena.EscribirResenaViewModel
import com.example.login.Grupos.GruposViewModel
import com.example.login.Home.HomeViewModel
import com.example.login.Perfil.ProfileViewModel
import com.example.login.Resena.ResenaViewModel

@Composable
fun AppNavegacion(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route,
        modifier         = modifier
    ) {

        // ── 1. Login ──
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.loginExitoso) {
                if (state.loginExitoso) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    viewModel.resetLoginExitoso()
                }
            }

            LaunchedEffect(state.irARegistro) {
                if (state.irARegistro) {
                    navController.navigate(Screen.Registro.route)
                    viewModel.resetIrARegistro()
                }
            }

            LoginScreen(
                viewModel             = viewModel,
                onForgotPasswordClick = { /* TODO */ }
            )
        }

        // ── 2. Registro ──
        composable(Screen.Registro.route) {
            val viewModel: RegistroViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.registroExitoso) {
                if (state.registroExitoso) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    viewModel.resetRegistroExitoso()
                }
            }

            LaunchedEffect(state.selectedTab) {
                if (state.selectedTab == 0) {
                    navController.popBackStack()
                }
            }

            RegistroScreen(
                viewModel     = viewModel,
                onGoogleClick = { /* TODO: Google Sign-In */ }
            )
        }

        // ── 3. Home ──
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel      = viewModel,
                onAlbumClick   = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(albumId))
                },
                onArtistaClick = { },
                onSearchClick  = { },
                onProfileClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // ── 4. Biblioteca ──
        composable(Screen.Biblioteca.route) {
            val viewModel: BibliotecaViewModel = hiltViewModel()
            BibliotecaScreen(
                viewModel            = viewModel,
                onPlaylistClick      = { },
                onCrearPlaylistClick = { }
            )
        }

        // ── 5. Descubre ──
        composable(Screen.Descubre.route) {
            val viewModel: DescubreViewModel = hiltViewModel()
            DescubreScreen(
                viewModel        = viewModel,
                onCategoriaClick = { },
                onGeneroClick    = { },
                onAlbumClick     = { album ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(album.id + 100))
                },
                onSearchClick  = { },
                onProfileClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // ── 6. Grupos ──
        composable(Screen.Grupos.route) {
            val viewModel: GruposViewModel = hiltViewModel()
            GruposScreen(
                viewModel    = viewModel,
                onGrupoClick = { navController.navigate(Screen.Chat.route) }
            )
        }

        // ── 7. Chat ── (ahora tiene acceso a Perfil)
        composable(Screen.Chat.route) {
            val viewModel: ChatViewModel = hiltViewModel()
            ChatScreen(
                viewModel      = viewModel,
                onBackClick    = { navController.popBackStack() },
                onProfileClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // ── 8. Reseñas ──
        composable(
            route     = Screen.Resena.route,
            arguments = listOf(navArgument("albumId") { type = NavType.IntType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getInt("albumId") ?: 0
            val viewModel: ResenaViewModel = hiltViewModel()
            LaunchedEffect(albumId) { viewModel.cargarResenas(albumId) }
            ResenaScreen(
                viewModel             = viewModel,
                albumId               = albumId,
                onBackClick           = { navController.popBackStack() },
                onResenaClick         = { resena ->
                    navController.navigate(Screen.Comentarios.createRoute(resena.id))
                },
                onEscribirResenaClick = { navController.navigate(Screen.EscribirResena.route) }
            )
        }

        // ── 9. Escribir Reseña ──
        composable(Screen.EscribirResena.route) {
            val viewModel: EscribirResenaViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(state.publicadoExitoso) {
                if (state.publicadoExitoso) {
                    navController.popBackStack()
                    viewModel.resetPublicado()
                }
            }
            EscribirResenaScreen(
                viewModel       = viewModel,
                onBackClick     = { navController.popBackStack() },
                onPublicarClick = { _, _ -> viewModel.publicarResena() }
            )
        }

        // ── 10. Perfil ──
        composable(Screen.Perfil.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel              = viewModel,
                onSearchClick          = { },
                onEditProfileClick     = { },
                onSiguiendoClick       = { },
                onMessageClick         = { navController.navigate(Screen.Chat.route) },
                onAlbumClick           = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(albumId))
                },
                onVerTodasResenasClick = { },
                onResenaClick          = { resena ->
                    navController.navigate(Screen.Comentarios.createRoute(resena.id))
                }
            )
        }

        // ── 11. Comentarios ──
        composable(
            route     = Screen.Comentarios.route,
            arguments = listOf(navArgument("resenaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val resenaId = backStackEntry.arguments?.getInt("resenaId") ?: 0
            val viewModel: ComentariosViewModel = hiltViewModel()
            LaunchedEffect(resenaId) { viewModel.cargarComentarios(resenaId) }
            ComentariosScreen(
                viewModel   = viewModel,
                resenaId    = resenaId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── 12. Detalle de Álbum ──
        composable(
            route     = Screen.AlbumDetalle.route,
            arguments = listOf(navArgument("albumId") { type = NavType.IntType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getInt("albumId") ?: 0
            val viewModel: AlbumDetalleViewModel = hiltViewModel()
            LaunchedEffect(albumId) { viewModel.cargarAlbum(albumId) }
            AlbumDetalleScreen(
                viewModel         = viewModel,
                albumId           = albumId,
                onBackClick       = { navController.popBackStack() },
                onVerResenasClick = {
                    navController.navigate(Screen.Resena.createRoute(albumId))
                }
            )
        }
    }
}