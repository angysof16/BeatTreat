package com.example.beattreat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.beattreat.navigation.AppNavegacion
import com.example.beattreat.navigation.Screen
import com.example.beattreat.ui.components.NotificationPermissionHandler

/**
 * BeatTreatApp actualizado para el Sprint 3.
 *
 * Cambios:
 *  1. NotificationPermissionHandler → pide permiso de notificaciones al inicio.
 *  2. onAddClick reemplazado por onFeedSiguiendoClick → navega al feed en tiempo real.
 *  3. La ruta "feed_siguiendo" se muestra con el bottom bar visible.
 */
@Composable
fun BeatTreatApp() {
    val navController = rememberNavController()
    val currentRoute  by navController.currentBackStackEntryAsState()
    val rutaActual    = currentRoute?.destination?.route

    val pantallasOcultas = listOf(
        Screen.Login.route,
        Screen.Registro.route
    )
    val mostrarBottomBar = rutaActual !in pantallasOcultas

    // ── Sprint 3: Pedir permiso de notificaciones push ──
    // El profesor dijo: "apenas se abra mi aplicación pues se muestre esta
    // actividad de por acá pidiéndole el permiso al usuario"
    NotificationPermissionHandler(
        onPermissionGranted = { /* El token se registra en Login/Registro */ },
        onPermissionDenied  = { /* Sin notificaciones, pero la app sigue funcionando */ }
    )

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            if (mostrarBottomBar) {
                BottomNavigationBar(
                    rutaActual           = rutaActual ?: "",
                    onHomeClick          = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onBibliotecaClick    = {
                        navController.navigate(Screen.Biblioteca.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onDescubreClick      = {
                        navController.navigate(Screen.Descubre.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onChatClick          = { navController.navigate(Screen.Grupos.route) },
                    // ── Sprint 3: navega al feed de siguiendo ──
                    onFeedSiguiendoClick = {
                        navController.navigate("feed_siguiendo") {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavegacion(
            navController = navController,
            modifier      = Modifier.padding(innerPadding)
        )
    }
}
