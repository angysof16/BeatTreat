package com.example.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.login.BottomNavigationBar
import com.example.login.navigation.AppNavegacion
import com.example.login.navigation.Screen

/**
 * Componente principal de la aplicación BeatTreat
 * Maneja la navegación y el scaffold global
 */
@Composable
fun BeatTreatApp() {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val rutaActual = currentRoute?.destination?.route

    // Pantallas donde NO se muestra el BottomBar
    val pantallasOcultas = listOf(
        Screen.Login.route,
        Screen.Registro.route
    )
    val mostrarBottomBar = rutaActual !in pantallasOcultas

    // ── Único Scaffold de toda la app ──
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (mostrarBottomBar) {
                BottomNavigationBar(
                    rutaActual = rutaActual ?: "",
                    onHomeClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onBibliotecaClick = {
                        navController.navigate(Screen.Biblioteca.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onDescubreClick = {
                        navController.navigate(Screen.Descubre.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onChatClick = {
                        navController.navigate(Screen.Chat.route)
                    },
                    onAddClick = {
                        navController.navigate(Screen.EscribirResena.route)
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavegacion(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}