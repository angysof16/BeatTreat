package com.example.login.Buscar

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun BuscarScreen(
    onBackClick: () -> Unit = {},
    onAlbumClick: (Int) -> Unit = {},
    onArtistaClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BuscarViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    BuscarScreenContent(
        uiState         = uiState,
        onBackClick     = onBackClick,
        onQueryChange   = { viewModel.onQueryChange(it) },
        onAlbumClick    = onAlbumClick,
        onArtistaClick  = onArtistaClick,
        onLimpiarClick  = { viewModel.limpiar() },
        modifier        = modifier
    )
}

// ── Stateless ──
@Composable
fun BuscarScreenContent(
    uiState: BuscarUIState,
    onBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onAlbumClick: (Int) -> Unit,
    onArtistaClick: (Int) -> Unit,
    onLimpiarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── TopBar con campo de búsqueda ──
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            TextField(
                value         = uiState.query,
                onValueChange = onQueryChange,
                placeholder   = { Text("Buscar álbumes, artistas...", color = Color.White.copy(alpha = 0.6f)) },
                modifier      = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                colors        = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White,
                    cursorColor             = Color.White
                ),
                singleLine    = true,
                leadingIcon   = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                },
                trailingIcon  = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = onLimpiarClick) {
                            Icon(Icons.Filled.Close, contentDescription = "Limpiar", tint = Color.White)
                        }
                    }
                }
            )
        }

        LazyColumn(
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Estado vacío ──
            if (uiState.query.isBlank()) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint               = Color.White.copy(alpha = 0.2f),
                            modifier           = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text     = "Busca tus álbumes y artistas favoritos",
                            color    = Color.White.copy(alpha = 0.4f),
                            fontSize = 15.sp
                        )
                    }
                }
                return@LazyColumn
            }

            // ── Sin resultados ──
            if (uiState.resultadosAlbumes.isEmpty() && uiState.resultadosArtistas.isEmpty()) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint               = Color.White.copy(alpha = 0.2f),
                            modifier           = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text     = "Sin resultados para \"${uiState.query}\"",
                            color    = Color.White.copy(alpha = 0.5f),
                            fontSize = 15.sp
                        )
                    }
                }
                return@LazyColumn
            }

            // ── Sección Artistas ──
            if (uiState.resultadosArtistas.isNotEmpty()) {
                item {
                    Text(
                        text       = "Artistas",
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(uiState.resultadosArtistas) { artista ->
                    ResultadoArtistaItem(
                        nombre  = artista.nombre,
                        onClick = { onArtistaClick(artista.id) }
                    )
                }
            }

            // ── Sección Álbumes ──
            if (uiState.resultadosAlbumes.isNotEmpty()) {
                item {
                    Text(
                        text       = "Álbumes",
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(uiState.resultadosAlbumes) { album ->
                    ResultadoAlbumItem(
                        nombre   = album.nombre,
                        artista  = album.artista,
                        onClick  = { onAlbumClick(album.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ResultadoArtistaItem(
    nombre: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BeatTreatColors.SurfaceVariant)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(BeatTreatColors.Purple40),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = nombre, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ResultadoAlbumItem(
    nombre: String,
    artista: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BeatTreatColors.SurfaceVariant)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BeatTreatColors.PurpleDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Album, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = nombre,  color = Color.White,                    fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = artista, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
    }
}

@Preview(showBackground = true)
@Composable
fun BuscarScreenPreview() {
    BeatTreatTheme {
        BuscarScreenContent(
            uiState        = BuscarUIState(),
            onBackClick    = {},
            onQueryChange  = {},
            onAlbumClick   = {},
            onArtistaClick = {},
            onLimpiarClick = {}
        )
    }
}