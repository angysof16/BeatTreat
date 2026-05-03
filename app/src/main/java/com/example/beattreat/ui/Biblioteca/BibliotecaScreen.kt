package com.example.beattreat.ui.Biblioteca

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.beattreat.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun BibliotecaScreen(
    onPlaylistClick: (PlaylistUI) -> Unit = {},
    onCrearPlaylistClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BibliotecaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    BibliotecaScreenContent(
        uiState              = uiState,
        playlists            = viewModel.playlistsFiltradas(),
        onSearchChange       = { viewModel.onSearchQueryChange(it) },
        onPlaylistClick      = onPlaylistClick,
        onCrearPlaylistClick = onCrearPlaylistClick,
        modifier             = modifier
    )
}

// ── Stateless ──
@Composable
fun BibliotecaScreenContent(
    uiState: BibliotecaUIState,
    playlists: List<PlaylistUI>,
    onSearchChange: (String) -> Unit,
    onPlaylistClick: (PlaylistUI) -> Unit,
    onCrearPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cancionesGuardadas = uiState.cancionesGuardadas ?: return
    val artistas           = uiState.artistas           ?: return
    val albumes            = uiState.albumes            ?: return

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF1A1A1A))) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── Header con imagen de fondo ──
            item {
                Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    AsyncImage(
                        model              = "HTTPS://PLACEHOLDER.COM/BIBLIOTECA/HEADER_BACKGROUND.JPG",
                        contentDescription = "Header",
                        modifier           = Modifier.fillMaxWidth().height(280.dp),
                        contentScale       = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 1f),
                            0.6f to Color.Transparent,
                            1.0f to Color(0xFF1A1A1A)
                        ))
                    ))
                    TextField(
                        value         = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        placeholder   = { Text("Buscar playlists en tu biblioteca", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 48.dp)
                            .clip(RoundedCornerShape(24.dp)).align(Alignment.TopCenter),
                        colors        = TextFieldDefaults.colors(
                            focusedContainerColor   = Color.White.copy(alpha = 0.4f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.4f),
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor        = Color.White,
                            unfocusedTextColor      = Color.White
                        ),
                        singleLine = true
                    )
                }
            }

            item { Text("Biblioteca", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

            item { ItemBiblioteca(imagenUrl = cancionesGuardadas.imagenUrl, titulo = cancionesGuardadas.titulo, subtitulo = "${cancionesGuardadas.cantidad} canciones", showCloud = true, onClick = {}) }
            item { ItemBiblioteca(imagenUrl = artistas.imagenUrl,           titulo = artistas.nombre,           subtitulo = "${artistas.cantidad} artistas",           showCloud = false, onClick = {}) }
            item { ItemBiblioteca(imagenUrl = albumes.imagenUrl,            titulo = albumes.titulo,            subtitulo = "${albumes.cantidad} álbumes",             showCloud = false, onClick = {}) }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Playlists", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Filled.ViewList, contentDescription = "Ver lista", tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            items(playlists) { playlist ->
                PlaylistItem(playlist = playlist, onClick = { onPlaylistClick(playlist) })
            }

            item { CrearPlaylistItem(onClick = onCrearPlaylistClick) }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ItemBiblioteca(imagenUrl: String, titulo: String, subtitulo: String, showCloud: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2A2A2A))) {
            AsyncImage(model = imagenUrl, contentDescription = titulo, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo,    color = Color.White,                    fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitulo, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
        if (showCloud) {
            Icon(Icons.Filled.Cloud, contentDescription = "Cloud", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun CrearPlaylistItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2A2A2A)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, contentDescription = "Crear playlist", tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text("Crear playlist", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PlaylistItem(playlist: PlaylistUI, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2A2A2A))) {
                AsyncImage(model = playlist.imagenUrl, contentDescription = playlist.nombre, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(playlist.nombre,      color = Color.White,                    fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(playlist.descripcion, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BibliotecaScreenPreview() {
    BeatTreatTheme {
        BibliotecaScreenContent(BibliotecaUIState(), emptyList(), {}, {}, {})
    }
}