package com.example.beattreat.ui.GeneroDetalle

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beattreat.ui.theme.BeatTreatColors
import com.example.beattreat.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun GeneroDetalleScreen(
    generoId: Int,
    onBackClick: () -> Unit = {},
    onAlbumClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GeneroDetalleViewModel
) {
    LaunchedEffect(generoId) { viewModel.cargarGenero(generoId) }

    val uiState by viewModel.uiState.collectAsState()

    GeneroDetalleScreenContent(
        uiState      = uiState,
        onBackClick  = onBackClick,
        onAlbumClick = onAlbumClick,
        modifier     = modifier
    )
}

// ── Stateless ──
@Composable
fun GeneroDetalleScreenContent(
    uiState: GeneroDetalleUIState,
    onBackClick: () -> Unit,
    onAlbumClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // ── Header coloreado ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(uiState.colorFondo), Color(uiState.colorFondo).copy(alpha = 0.4f), Color(0xFF0D0D0D))
                    )
                )
        ) {
            IconButton(
                onClick  = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) {
                Box(
                    modifier         = Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Text(text = "Género", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                Text(text = uiState.nombre, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        // ── Lista de álbumes del género ──
        if (uiState.albumes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay álbumes en este género aún", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(uiState.albumes) { album ->
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BeatTreatColors.SurfaceVariant)
                            .clickable { onAlbumClick(album.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier         = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(BeatTreatColors.PurpleDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Album, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = album.nombre,   color = Color.White,                    fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = album.artista,  color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneroDetalleScreenPreview() {
    BeatTreatTheme {
        GeneroDetalleScreenContent(
            uiState      = GeneroDetalleUIState(nombre = "Rock", colorFondo = 0xFF9333EA),
            onBackClick  = {},
            onAlbumClick = {}
        )
    }
}