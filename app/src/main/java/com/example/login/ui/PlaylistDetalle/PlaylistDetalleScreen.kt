package com.example.login.ui.PlaylistDetalle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun PlaylistDetalleScreen(
    playlistId: Int,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetalleViewModel
) {
    LaunchedEffect(playlistId) { viewModel.cargarPlaylist(playlistId) }

    val uiState by viewModel.uiState.collectAsState()

    PlaylistDetalleScreenContent(
        uiState     = uiState,
        onBackClick = onBackClick,
        modifier    = modifier
    )
}

// ── Stateless ──
@Composable
fun PlaylistDetalleScreenContent(
    uiState: PlaylistDetalleUIState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist = uiState.playlist ?: run {
        Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BeatTreatColors.Purple60)
        }
        return
    }

    LazyColumn(
        modifier       = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ── Header ──
        item {
            Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
                if (playlist.imagenRes != 0) {
                    Image(
                        painter            = painterResource(id = playlist.imagenRes),
                        contentDescription = playlist.nombre,
                        modifier           = Modifier.fillMaxSize(),
                        contentScale       = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(colors = listOf(BeatTreatColors.Purple60, BeatTreatColors.PurpleDark))
                    ), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.QueueMusic, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(100.dp))
                    }
                }
                // Gradiente
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(colorStops = arrayOf(0f to Color.Black.copy(0.35f), 0.5f to Color.Transparent, 1f to Color(0xFF0D0D0D)))
                ))
                // Botón volver
                IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
                    Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(0.45f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                // Portada centrada
                if (playlist.imagenRes != 0) {
                    Image(
                        painter            = painterResource(id = playlist.imagenRes),
                        contentDescription = playlist.nombre,
                        modifier           = Modifier.size(160.dp).align(Alignment.Center).clip(RoundedCornerShape(14.dp)),
                        contentScale       = ContentScale.Crop
                    )
                }
            }
        }

        // ── Info ──
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(text = playlist.nombre,       color = Color.White,                    fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = playlist.descripcion,  color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "${playlist.canciones.size} canciones", color = Color.White.copy(alpha = 0.45f), fontSize = 13.sp)
            }
        }

        // ── Canciones ──
        itemsIndexed(playlist.canciones) { index, cancion ->
            Row(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = (index + 1).toString(),
                    color      = Color.White.copy(alpha = 0.4f),
                    fontSize   = 13.sp,
                    modifier   = Modifier.width(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = cancion.titulo,  color = Color.White,                    fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text(text = cancion.artista, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
                Text(text = cancion.duracion, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
            if (index < playlist.canciones.lastIndex) {
                Divider(color = Color.White.copy(alpha = 0.06f), modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistDetalleScreenPreview() {
    BeatTreatTheme {
        PlaylistDetalleScreenContent(
            uiState     = PlaylistDetalleUIState(),
            onBackClick = {}
        )
    }
}