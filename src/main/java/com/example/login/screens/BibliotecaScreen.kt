package com.example.login.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.model.AlbumUI
import com.example.login.model.ArtistaUI
import com.example.login.model.BibliotecaData
import com.example.login.model.CancionGuardadaUI
import com.example.login.model.PlaylistUI
import com.example.login.ui.theme.BeatTreatTheme

// ── Estado de BibliotecaScreen (State Hoisting) ──
data class BibliotecaState(
    val searchQuery: String = "",
    val cancionesGuardadas: CancionGuardadaUI = BibliotecaData.cancionesGuardadas,
    val artistas: ArtistaUI = BibliotecaData.artistas,
    val albumes: AlbumUI = BibliotecaData.albumes,
    val playlists: List<PlaylistUI> = BibliotecaData.playlists
)

// ── Stateful: contiene el estado ──
@Composable
fun BibliotecaScreen(
    onPlaylistClick: (PlaylistUI) -> Unit = {},
    onCrearPlaylistClick: () -> Unit = {}
) {
    var state by remember { mutableStateOf(BibliotecaState()) }

    BibliotecaScreenContent(
        state = state,
        onSearchChange = { state = state.copy(searchQuery = it) },
        onPlaylistClick = onPlaylistClick,
        onCrearPlaylistClick = onCrearPlaylistClick
    )
}

// ── Stateless: solo recibe datos y emite eventos ──
@Composable
fun BibliotecaScreenContent(
    state: BibliotecaState,
    onSearchChange: (String) -> Unit,
    onPlaylistClick: (PlaylistUI) -> Unit,
    onCrearPlaylistClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3A5A7C),
                        Color(0xFF1A1A1A)
                    )
                )
            )
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.subtract),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Buscador ──
            item {
                TextField(
                    value = state.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "Buscar playlists en tu biblioteca",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )
            }

            // ── Título Biblioteca ──
            item {
                Text(
                    text = "Biblioteca",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // ── Canciones guardadas ──
            item {
                ItemBiblioteca(
                    imagenRes = R.drawable.albumgeneral,
                    titulo = state.cancionesGuardadas.titulo,
                    subtitulo = "${state.cancionesGuardadas.cantidad} canciones",
                    showCloud = true,
                    onClick = {}
                )
            }

            // ── Artistas ──
            item {
                ItemBiblioteca(
                    imagenRes = R.drawable.westcol,
                    titulo = state.artistas.nombre,
                    subtitulo = "${state.artistas.cantidad} artistas",
                    showCloud = false,
                    onClick = {}
                )
            }

            // ── Álbumes ──
            item {
                ItemBiblioteca(
                    imagenRes = R.drawable.agregarplaylist,
                    titulo = state.albumes.titulo,
                    subtitulo = "${state.albumes.cantidad} álbumes",
                    showCloud = false,
                    onClick = {}
                )
            }

            // ── Header Playlists ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playlists",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Filled.ViewList,
                        contentDescription = "Ver lista",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // ── Lista de Playlists ──
            items(state.playlists) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) }
                )
            }

            // ── Crear Playlist ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCrearPlaylistClick() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2A2A2A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Crear playlist",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Crear playlist",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Espacio para el BottomBar
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// ── Item de Biblioteca (Canciones, Artistas, Álbumes) ──
@Composable
fun ItemBiblioteca(
    imagenRes: Int,
    titulo: String,
    subtitulo: String,
    showCloud: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen
        Image(
            painter = painterResource(id = imagenRes),
            contentDescription = titulo,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Texto
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitulo,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }

        // Ícono de nube (solo para canciones guardadas)
        if (showCloud) {
            Icon(
                imageVector = Icons.Filled.Cloud,
                contentDescription = "Cloud",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ── Item de Playlist ──
@Composable
fun PlaylistItem(
    playlist: PlaylistUI,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Imagen placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2A2A))
            ) {
                if (playlist.imagenRes != 0) {
                    Image(
                        painter = painterResource(id = playlist.imagenRes),
                        contentDescription = playlist.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF3A3A3A))
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = playlist.nombre,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = playlist.descripcion,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }

        // Tres puntos
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Opciones",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BibliotecaScreenPreview() {
    BeatTreatTheme {
        BibliotecaScreen()
    }
}