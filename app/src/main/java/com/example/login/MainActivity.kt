package com.example.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.login.navigation.AppNavegacion
import com.example.login.navigation.Screen
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeatTreatTheme {
                AppScaffold()
            }
        }
    }
}

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val rutaActual = currentRoute?.destination?.route

    // Pantallas donde NO se muestra el BottomBar
    val pantallasOcultas = listOf(Screen.Login.route, Screen.Registro.route)
    val mostrarBottomBar = rutaActual !in pantallasOcultas

    // ── Único Scaffold de toda la app ──
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (mostrarBottomBar) {
                BottomBarGlobal(
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
                        navController.navigate(Screen.Grupos.route)
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

// ── BottomBar Global único ──
@Composable
fun BottomBarGlobal(
    rutaActual: String,
    onHomeClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onDescubreClick: () -> Unit,
    onChatClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BeatTreatColors.BottomBar)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onHomeClick) {
            Icon(
                Icons.Filled.Home,
                contentDescription = "Inicio",
                tint = if (rutaActual == Screen.Home.route) BeatTreatColors.Purple60 else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        IconButton(onClick = onBibliotecaClick) {
            Icon(
                Icons.Filled.Bookmark,
                contentDescription = "Biblioteca",
                tint = if (rutaActual == Screen.Biblioteca.route) BeatTreatColors.Purple60 else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        IconButton(onClick = onDescubreClick) {
            Icon(
                Icons.Filled.Explore,
                contentDescription = "Descubre",
                tint = if (rutaActual == Screen.Descubre.route) BeatTreatColors.Purple60 else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        IconButton(onClick = onChatClick) {
            Icon(
                Icons.Filled.ChatBubble,
                contentDescription = "Chats",
                tint = if (rutaActual == Screen.Chat.route) BeatTreatColors.Purple60 else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        IconButton(onClick = onAddClick) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Agregar",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}