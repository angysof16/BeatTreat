package com.example.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.login.navigation.Screen
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// Barra de navegación inferior global de la aplicación

@Composable
fun BottomNavigationBar(
    rutaActual: String,
    onHomeClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onDescubreClick: () -> Unit,
    onChatClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BeatTreatColors.BottomBar)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home
        BottomNavItem(
            icon = Icons.Filled.Home,
            contentDescription = "Inicio",
            isSelected = rutaActual == Screen.Home.route,
            onClick = onHomeClick
        )

        // Biblioteca
        BottomNavItem(
            icon = Icons.Filled.Bookmark,
            contentDescription = "Biblioteca",
            isSelected = rutaActual == Screen.Biblioteca.route,
            onClick = onBibliotecaClick
        )

        // Descubre
        BottomNavItem(
            icon = Icons.Filled.Explore,
            contentDescription = "Descubre",
            isSelected = rutaActual == Screen.Descubre.route,
            onClick = onDescubreClick
        )

        // Chat
        BottomNavItem(
            icon = Icons.Filled.ChatBubble,
            contentDescription = "Chats",
            isSelected = rutaActual == Screen.Chat.route,
            onClick = onChatClick
        )

        // Agregar (siempre blanco, nunca seleccionado)
        BottomNavItem(
            icon = Icons.Filled.Add,
            contentDescription = "Agregar",
            isSelected = false,
            onClick = onAddClick
        )
    }
}

/**
 * Item individual de la barra de navegación inferior
 *
 * @param icon Ícono a mostrar
 * @param contentDescription Descripción del ícono
 * @param isSelected Si el item está seleccionado
 * @param onClick Callback cuando se presiona
 */
@Composable
private fun BottomNavItem(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) BeatTreatColors.Purple60 else Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BeatTreatTheme {
        BottomNavigationBar(
            rutaActual = Screen.Home.route,
            onHomeClick = {},
            onBibliotecaClick = {},
            onDescubreClick = {},
            onChatClick = {},
            onAddClick = {}
        )
    }
}