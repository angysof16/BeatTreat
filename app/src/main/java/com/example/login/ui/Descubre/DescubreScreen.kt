package com.example.login.ui.Descubre

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.login.R
import com.example.login.ui.components.FotoPerfilTopBar
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Stateful ──
@Composable
fun DescubreScreen(
    onCategoriaClick: (CategoriaUI) -> Unit = {},
    onGeneroClick: (GeneroUI) -> Unit = {},
    onAlbumClick: (AlbumDescubreUI) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DescubreViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    DescubreScreenContent(uiState, onCategoriaClick, onGeneroClick, onAlbumClick, onSearchClick, onProfileClick, modifier)
}

// ── Stateless ──
@Composable
fun DescubreScreenContent(
    uiState: DescubreUIState,
    onCategoriaClick: (CategoriaUI) -> Unit,
    onGeneroClick: (GeneroUI) -> Unit,
    onAlbumClick: (AlbumDescubreUI) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarDescubre(fotoPerfilUrl = uiState.fotoPerfilUrl, onSearchClick = onSearchClick, onProfileClick = onProfileClick)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text("Descubre", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
            }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.categorias) { categoria ->
                        CategoriaCard(categoria = categoria, onClick = { onCategoriaClick(categoria) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item { SectionHeader(titulo = "Géneros", onVerMasClick = {}) }
            item { GeneroGrid(generos = uiState.generos, onGeneroClick = onGeneroClick); Spacer(modifier = Modifier.height(24.dp)) }
            item { SectionHeader(titulo = "Nuevos lanzamientos", onVerMasClick = {}) }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.nuevosLanzamientos) { album ->
                        AlbumCard(album = album, onClick = { onAlbumClick(album) })
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TopBarDescubre(fotoPerfilUrl: String, onSearchClick: () -> Unit, onProfileClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(bottomEnd = 12.dp)).background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center) {
            AsyncImage(
                model              = "https://cdn.phototourl.com/free/2026-04-16-f75c12f6-7aa0-4e5d-959f-803340165dd0.png",
                contentDescription = "Logo BeatTreat",
                modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                contentScale       = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier.weight(1f)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(bottomStart = 12.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("BeatTreat", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Normal,
                fontFamily = JaroFont, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                FotoPerfilTopBar(fotoPerfilUrl = fotoPerfilUrl, onClick = onProfileClick)
            }
        }
    }
}

@Composable
fun CategoriaCard(categoria: CategoriaUI, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.width(120.dp).height(100.dp).clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(colors = listOf(Color(categoria.colorFondo), Color(categoria.colorFondo).copy(alpha = 0.7f))))
            .clickable { onClick() },
        contentAlignment = Alignment.BottomStart
    ) {
        Text(categoria.nombre, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium,
            lineHeight = 14.sp, modifier = Modifier.padding(12.dp))
    }
}

@Composable
fun SectionHeader(titulo: String, onVerMasClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(titulo, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Filled.ArrowForward, contentDescription = "Ver más", tint = Color.White,
            modifier = Modifier.size(20.dp).clickable { onVerMasClick() })
    }
}

@Composable
fun GeneroGrid(generos: List<GeneroUI>, onGeneroClick: (GeneroUI) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        generos.chunked(2).forEach { rowGeneros ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowGeneros.forEach { genero ->
                    GeneroChip(genero = genero, onClick = { onGeneroClick(genero) }, modifier = Modifier.weight(1f))
                }
                if (rowGeneros.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GeneroChip(genero: GeneroUI, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(48.dp).clip(RoundedCornerShape(24.dp)).background(BeatTreatColors.SurfaceVariant).clickable { onClick() },
        contentAlignment = Alignment.CenterStart) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp)) {
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(genero.colorChip)))
            Text(genero.nombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp))
        }
    }
}

@Composable
fun AlbumCard(album: AlbumDescubreUI, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.width(140.dp).clickable { onClick() }) {
        Box(modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp)).background(BeatTreatColors.SurfaceVariant)) {
            AsyncImage(
                model              = album.imagenUrl,
                contentDescription = album.nombre,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(album.nombre,  color = Color.White,                    fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        Text(album.artista, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, maxLines = 1)
    }
}

@Preview(showBackground = true)
@Composable
fun DescubreScreenPreview() {
    BeatTreatTheme {
        DescubreScreenContent(DescubreUIState(), {}, {}, {}, {}, {})
    }
}