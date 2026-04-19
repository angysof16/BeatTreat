package com.example.login.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login.ui.AlbumDetalle.AlbumDetalleScreen
import com.example.login.ui.AlbumDetalle.AlbumDetalleViewModel
import com.example.login.ui.ArtistaDetalle.ArtistaDetalleScreen
import com.example.login.ui.ArtistaDetalle.ArtistaDetalleViewModel
import com.example.login.ui.Biblioteca.BibliotecaScreen
import com.example.login.ui.Biblioteca.BibliotecaViewModel
import com.example.login.ui.Buscar.BuscarScreen
import com.example.login.ui.Buscar.BuscarViewModel
import com.example.login.ui.Chat.ChatScreen
import com.example.login.ui.Chat.ChatViewModel
import com.example.login.ui.Comentarios.ComentariosScreen
import com.example.login.ui.Comentarios.ComentariosViewModel
import com.example.login.ui.Descubre.DescubreScreen
import com.example.login.ui.Descubre.DescubreViewModel
import com.example.login.ui.EditarPerfil.EditarPerfilScreen
import com.example.login.ui.EditarPerfil.EditarPerfilViewModel
import com.example.login.ui.EscribirResena.EscribirResenaScreen
import com.example.login.ui.EscribirResena.EscribirResenaViewModel
import com.example.login.ui.GeneroDetalle.GeneroDetalleScreen
import com.example.login.ui.GeneroDetalle.GeneroDetalleViewModel
import com.example.login.ui.Grupos.GruposScreen
import com.example.login.ui.Grupos.GruposViewModel
import com.example.login.ui.Home.HomeScreen
import com.example.login.ui.Home.HomeViewModel
import com.example.login.ui.Login.LoginScreen
import com.example.login.ui.Login.LoginViewModel
import com.example.login.ui.MiPerfil.MiPerfilScreen
import com.example.login.ui.Perfil.ProfileScreen
import com.example.login.ui.Perfil.ProfileViewModel
import com.example.login.ui.PerfilOtroUsuario.PerfilOtroUsuarioScreen
import com.example.login.ui.PerfilOtroUsuario.PerfilOtroUsuarioViewModel
import com.example.login.ui.PlaylistDetalle.PlaylistDetalleScreen
import com.example.login.ui.PlaylistDetalle.PlaylistDetalleViewModel
import com.example.login.ui.Registro.RegistroScreen
import com.example.login.ui.Registro.RegistroViewModel
import com.example.login.ui.Resena.ResenaScreen
import com.example.login.ui.Resena.ResenaViewModel
import com.example.login.ui.Seguidores.SeguidoresScreen
import com.example.login.ui.Seguidores.SeguidoresViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.login.util.FirestoreSeedHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavegacion(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Seed de datos iniciales al arrancar
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            FirestoreSeedHelper.seedIfEmpty(FirebaseFirestore.getInstance())
        }
    }

    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        Screen.Perfil.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController    = navController,
        startDestination = startDestination,
        modifier         = modifier
    ) {

        // ── 1. Login ──────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.loginExitoso) {
                if (state.loginExitoso) {
                    navController.navigate(Screen.Perfil.route) {
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
            LoginScreen(viewModel = viewModel, onForgotPasswordClick = {})
        }

        // ── 2. Registro ───────────────────────────────────────────────────────
        composable(Screen.Registro.route) {
            val viewModel: RegistroViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.registroExitoso) {
                if (state.registroExitoso) {
                    navController.navigate(Screen.Perfil.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    viewModel.resetRegistroExitoso()
                }
            }
            LaunchedEffect(state.selectedTab) {
                if (state.selectedTab == 0) navController.popBackStack()
            }
            RegistroScreen(viewModel = viewModel, onGoogleClick = {})
        }

        // ── 3. Home ───────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val lifecycle = it.lifecycle
            DisposableEffect(lifecycle) {
                val obs = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) viewModel.refrescarFotoPerfil()
                }
                lifecycle.addObserver(obs)
                onDispose { lifecycle.removeObserver(obs) }
            }
            HomeScreen(
                viewModel      = viewModel,
                onAlbumClick   = { hashId ->
                    val firestoreId = viewModel.getFirestoreId(hashId) ?: hashId.toString()
                    val encoded = URLEncoder.encode(firestoreId, "UTF-8")
                    navController.navigate(Screen.AlbumDetalle.createRoute(encoded))
                },
                onArtistaClick = { artistaId ->
                    navController.navigate(Screen.ArtistaDetalle.createRoute(artistaId))
                },
                onSearchClick  = { navController.navigate(Screen.Buscar.route) },
                onProfileClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // ── 4. Biblioteca ─────────────────────────────────────────────────────
        composable(Screen.Biblioteca.route) {
            val viewModel: BibliotecaViewModel = hiltViewModel()
            BibliotecaScreen(
                viewModel            = viewModel,
                onPlaylistClick      = { navController.navigate(Screen.PlaylistDetalle.createRoute(it.id)) },
                onCrearPlaylistClick = { navController.navigate(Screen.CrearPlaylist.route) }
            )
        }

        // ── 5. Descubre ───────────────────────────────────────────────────────
        composable(Screen.Descubre.route) {
            val viewModel: DescubreViewModel = hiltViewModel()
            val lifecycle = it.lifecycle
            DisposableEffect(lifecycle) {
                val obs = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) viewModel.refrescarFotoPerfil()
                }
                lifecycle.addObserver(obs)
                onDispose { lifecycle.removeObserver(obs) }
            }
            DescubreScreen(
                viewModel        = viewModel,
                onCategoriaClick = { categoria ->
                    navController.navigate(Screen.CategoriaDetalle.createRoute(categoria.id))
                },
                onGeneroClick    = { genero ->
                    navController.navigate(Screen.GeneroDetalle.createRoute(genero.id))
                },
                onAlbumClick     = { album ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(
                        URLEncoder.encode((album.id + 100).toString(), "UTF-8")
                    ))
                },
                onSearchClick    = { navController.navigate(Screen.Buscar.route) },
                onProfileClick   = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // ── 6. Grupos ─────────────────────────────────────────────────────────
        composable(Screen.Grupos.route) {
            val viewModel: GruposViewModel = hiltViewModel()
            val lifecycle = it.lifecycle
            DisposableEffect(lifecycle) {
                val obs = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) viewModel.refrescarFotoPerfil()
                }
                lifecycle.addObserver(obs)
                onDispose { lifecycle.removeObserver(obs) }
            }
            GruposScreen(
                viewModel      = viewModel,
                onGrupoClick   = { navController.navigate(Screen.Chat.route) },
                onSearchClick  = { navController.navigate(Screen.Buscar.route) },
                onProfileClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // ── 7. Chat ───────────────────────────────────────────────────────────
        composable(Screen.Chat.route) {
            val viewModel: ChatViewModel = hiltViewModel()
            ChatScreen(
                viewModel      = viewModel,
                onBackClick    = { navController.popBackStack() },
                onProfileClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // ── 8. Buscar ─────────────────────────────────────────────────────────
        composable(Screen.Buscar.route) {
            val viewModel: BuscarViewModel = hiltViewModel()
            BuscarScreen(
                viewModel      = viewModel,
                onBackClick    = { navController.popBackStack() },
                onAlbumClick   = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(
                        URLEncoder.encode(albumId.toString(), "UTF-8")
                    ))
                },
                onArtistaClick = { artistaId ->
                    navController.navigate(Screen.ArtistaDetalle.createRoute(artistaId))
                }
            )
        }

        // ── 9. Reseñas ────────────────────────────────────────────────────────
        composable(
            route     = Screen.Resena.route,
            arguments = listOf(navArgument("albumId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedId        = backStackEntry.arguments?.getString("albumId") ?: ""
            val firestoreAlbumId = URLDecoder.decode(encodedId, "UTF-8")
            val viewModel: ResenaViewModel = hiltViewModel()
            LaunchedEffect(firestoreAlbumId) { viewModel.cargarResenas(firestoreAlbumId) }

            ResenaScreen(
                viewModel             = viewModel,
                albumId               = firestoreAlbumId.hashCode(),
                onBackClick           = { navController.popBackStack() },
                onResenaClick         = { resena ->
                    navController.navigate(Screen.Comentarios.createRoute(resena.id))
                },
                // ← FIX: onAutorClick recibe el UID real de Firebase (String)
                onAutorClick          = { autorFirestoreUserId ->
                    if (autorFirestoreUserId.isNotBlank()) {
                        navController.navigate(Screen.PerfilOtroUsuario.createRoute(
                            URLEncoder.encode(autorFirestoreUserId, "UTF-8")
                        ))
                    }
                },
                onEscribirResenaClick = {
                    val encoded = URLEncoder.encode(firestoreAlbumId, "UTF-8")
                    navController.navigate(Screen.EscribirResena.createRoute(encoded))
                }
            )
        }

        // ── 10. Escribir Reseña ────────────────────────────────────────────────
        composable(
            route     = Screen.EscribirResena.route,
            arguments = listOf(
                navArgument("albumId") {
                    type         = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val encodedId   = backStackEntry.arguments?.getString("albumId") ?: ""
            val firestoreId = URLDecoder.decode(encodedId, "UTF-8")
            val viewModel: EscribirResenaViewModel = hiltViewModel()

            LaunchedEffect(firestoreId) {
                if (firestoreId.isNotBlank()) viewModel.preSeleccionarAlbum(firestoreId)
            }

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

        // ── 11. Mi Perfil ─────────────────────────────────────────────────────
        composable(Screen.MiPerfil.route) {
            MiPerfilScreen(
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(
                        URLEncoder.encode(albumId.toString(), "UTF-8")
                    ))
                }
            )
        }

        // ── 12. Perfil propio ─────────────────────────────────────────────────
        composable(Screen.Perfil.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val lifecycle = it.lifecycle
            DisposableEffect(lifecycle) {
                val obs = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) viewModel.refrescarFotoPerfil()
                }
                lifecycle.addObserver(obs)
                onDispose { lifecycle.removeObserver(obs) }
            }
            ProfileScreen(
                viewModel              = viewModel,
                onSearchClick          = { navController.navigate(Screen.Buscar.route) },
                onEditProfileClick     = { navController.navigate(Screen.EditarPerfil.route) },
                onSiguiendoClick       = { navController.navigate(Screen.Seguidores.createRoute("siguiendo")) },
                onSeguidoresClick      = { navController.navigate(Screen.Seguidores.createRoute("seguidores")) },
                onMessageClick         = { navController.navigate(Screen.Chat.route) },
                onAlbumClick           = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(
                        URLEncoder.encode(albumId.toString(), "UTF-8")
                    ))
                },
                onVerTodasResenasClick = { navController.navigate(Screen.MiPerfil.route) },
                onResenaClick          = { resena ->
                    navController.navigate(Screen.Comentarios.createRoute(resena.id))
                },
                onCerrarSesionClick    = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ── 13. Comentarios ───────────────────────────────────────────────────
        composable(
            route     = Screen.Comentarios.route,
            arguments = listOf(navArgument("resenaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val resenaId  = backStackEntry.arguments?.getInt("resenaId") ?: 0
            val viewModel: ComentariosViewModel = hiltViewModel()
            LaunchedEffect(resenaId) { viewModel.cargarComentarios(resenaId) }
            ComentariosScreen(
                viewModel    = viewModel,
                resenaId     = resenaId,
                onBackClick  = { navController.popBackStack() }
            )
        }

        // ── 14. Detalle de Álbum ──────────────────────────────────────────────
        composable(
            route     = Screen.AlbumDetalle.route,
            arguments = listOf(navArgument("albumId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedId   = backStackEntry.arguments?.getString("albumId") ?: ""
            val firestoreId = URLDecoder.decode(encodedId, "UTF-8")
            val viewModel: AlbumDetalleViewModel = hiltViewModel()
            LaunchedEffect(firestoreId) { viewModel.cargarAlbum(firestoreId) }

            AlbumDetalleScreen(
                albumId           = firestoreId.hashCode(),
                onBackClick       = { navController.popBackStack() },
                onVerResenasClick = {
                    val encoded = URLEncoder.encode(firestoreId, "UTF-8")
                    navController.navigate(Screen.Resena.createRoute(encoded))
                },
                viewModel         = viewModel
            )
        }

        // ── 15. Detalle de Artista ────────────────────────────────────────────
        composable(
            route     = Screen.ArtistaDetalle.route,
            arguments = listOf(navArgument("artistaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val artistaId = backStackEntry.arguments?.getInt("artistaId") ?: 0
            val viewModel: ArtistaDetalleViewModel = hiltViewModel()
            LaunchedEffect(artistaId) { viewModel.cargarArtista(artistaId) }
            ArtistaDetalleScreen(
                viewModel    = viewModel,
                artistaId    = artistaId,
                onBackClick  = { navController.popBackStack() },
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(
                        URLEncoder.encode(albumId.toString(), "UTF-8")
                    ))
                }
            )
        }

        // ── 16. Detalle de Género ─────────────────────────────────────────────
        composable(
            route     = Screen.GeneroDetalle.route,
            arguments = listOf(navArgument("generoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val generoId  = backStackEntry.arguments?.getInt("generoId") ?: 0
            val viewModel: GeneroDetalleViewModel = hiltViewModel()
            LaunchedEffect(generoId) { viewModel.cargarGenero(generoId) }
            GeneroDetalleScreen(
                viewModel    = viewModel,
                generoId     = generoId,
                onBackClick  = { navController.popBackStack() },
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(
                        URLEncoder.encode(albumId.toString(), "UTF-8")
                    ))
                }
            )
        }

        // ── 17. Detalle de Categoría ──────────────────────────────────────────
        composable(
            route     = Screen.CategoriaDetalle.route,
            arguments = listOf(navArgument("categoriaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getInt("categoriaId") ?: 0
            val viewModel: GeneroDetalleViewModel = hiltViewModel()
            LaunchedEffect(categoriaId) { viewModel.cargarPorCategoria(categoriaId) }
            GeneroDetalleScreen(
                viewModel    = viewModel,
                generoId     = categoriaId,
                onBackClick  = { navController.popBackStack() },
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetalle.createRoute(
                        URLEncoder.encode(albumId.toString(), "UTF-8")
                    ))
                }
            )
        }

        // ── 18. Detalle de Playlist ───────────────────────────────────────────
        composable(
            route     = Screen.PlaylistDetalle.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.IntType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getInt("playlistId") ?: 0
            val viewModel: PlaylistDetalleViewModel = hiltViewModel()
            LaunchedEffect(playlistId) { viewModel.cargarPlaylist(playlistId) }
            PlaylistDetalleScreen(
                viewModel  = viewModel,
                playlistId = playlistId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── 19. Editar Perfil ─────────────────────────────────────────────────
        composable(Screen.EditarPerfil.route) {
            val viewModel: EditarPerfilViewModel = hiltViewModel()
            EditarPerfilScreen(
                viewModel      = viewModel,
                onBackClick    = { navController.popBackStack() },
                onGuardarClick = { navController.popBackStack() }
            )
        }

        // ── 20. Seguidores / Siguiendo ────────────────────────────────────────
        composable(
            route     = Screen.Seguidores.route,
            arguments = listOf(navArgument("tipo") { type = NavType.StringType })
        ) { backStackEntry ->
            val tipo      = backStackEntry.arguments?.getString("tipo") ?: "seguidores"
            val viewModel: SeguidoresViewModel = hiltViewModel()
            LaunchedEffect(tipo) { viewModel.cargar(tipo) }
            SeguidoresScreen(
                viewModel   = viewModel,
                tipo        = tipo,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── 21. Crear Playlist ────────────────────────────────────────────────
        composable(Screen.CrearPlaylist.route) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text("Crear Playlist — Próximamente", color = Color.White)
            }
        }

        // ── 22. Perfil de OTRO usuario (recibe String firestoreUserId) ─────────
        composable(
            route     = Screen.PerfilOtroUsuario.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedId = backStackEntry.arguments?.getString("userId") ?: ""
            val userId    = URLDecoder.decode(encodedId, "UTF-8")
            val viewModel: PerfilOtroUsuarioViewModel = hiltViewModel()
            LaunchedEffect(userId) { viewModel.cargarPerfil(userId) }
            PerfilOtroUsuarioScreen(
                viewModel   = viewModel,
                userId      = userId.toIntOrNull() ?: userId.hashCode(),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}