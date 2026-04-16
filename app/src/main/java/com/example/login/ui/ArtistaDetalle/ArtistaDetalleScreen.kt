package com.example.login.ui.ArtistaDetalle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun ArtistaDetalleScreen(
    artistaId: Int,
    onBackClick: () -> Unit = {},
    onAlbumClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ArtistaDetalleViewModel
) {
    LaunchedEffect(artistaId) { viewModel.cargarArtista(artistaId) }
    val uiState by viewModel.uiState.collectAsState()

    ArtistaDetalleScreenContent(
        uiState       = uiState,
        onBackClick   = onBackClick,
        onAlbumClick  = onAlbumClick,
        onSeguirClick = { viewModel.toggleSeguir() },
        modifier      = modifier
    )
}

// ── Stateless ──
@Composable
fun ArtistaDetalleScreenContent(
    uiState: ArtistaDetalleUIState,
    onBackClick: () -> Unit,
    onAlbumClick: (Int) -> Unit,
    onSeguirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val artista = uiState.artista ?: run {
        Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BeatTreatColors.Purple60)
        }
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentPadding = PaddingValues(bottom = 100.dp)) {

        // ── Header ──
        item {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(colors = listOf(BeatTreatColors.Purple40, BeatTreatColors.PurpleDark, Color(0xFF121212)))
                ), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(140.dp))
                }
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(colorStops = arrayOf(0.4f to Color.Transparent, 1f to Color(0xFF0D0D0D)))
                ))
                IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
                    Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                    Text(artista.nombre, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    Text("${artista.albumes.size} álbumes", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }
        }

        // ── Botón Seguir ──
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(24.dp))
                        .background(if (uiState.siguiendo) BeatTreatColors.SurfaceVariant else BeatTreatColors.Purple60)
                        .clickable { onSeguirClick() }.padding(horizontal = 28.dp, vertical = 10.dp)
                ) {
                    Text(if (uiState.siguiendo) "Siguiendo" else "Seguir", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── Discografía ──
        item { Text("Discografía", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) }

        items(artista.albumes) { album ->
            Row(
                modifier          = Modifier.fillMaxWidth().clickable { onAlbumClick(album.id) }.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(BeatTreatColors.SurfaceVariant),
                    contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model              = album.imagenUrl,
                        contentDescription = album.nombre,
                        modifier           = Modifier.fillMaxSize(),
                        contentScale       = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(album.nombre,  color = Color.White,                    fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text(artista.nombre, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
            }
            Divider(color = Color.White.copy(alpha = 0.06f), modifier = Modifier.padding(horizontal = 20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistaDetalleScreenPreview() {
    BeatTreatTheme {
        ArtistaDetalleScreenContent(uiState = ArtistaDetalleUIState(), onBackClick = {}, onAlbumClick = {}, onSeguirClick = {})
    }
}