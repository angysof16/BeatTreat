package com.example.login.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.model.AlbumDescubreUI
import com.example.login.model.CategoriaUI
import com.example.login.model.DescubreData
import com.example.login.model.GeneroUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Estado de DescubreScreen (State Hoisting) ──
data class DescubreState(
    val categorias: List<CategoriaUI> = DescubreData.categorias,
    val generos: List<GeneroUI> = DescubreData.generos,
    val nuevosLanzamientos: List<AlbumDescubreUI> = DescubreData.nuevosLanzamientos
)

// ── Stateful ──
@Composable
fun DescubreScreen(
    onCategoriaClick: (CategoriaUI) -> Unit = {},
    onGeneroClick: (GeneroUI) -> Unit = {},
    onAlbumClick: (AlbumDescubreUI) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state = remember { DescubreState() }

    DescubreScreenContent(
        state = state,
        onCategoriaClick = onCategoriaClick,
        onGeneroClick = onGeneroClick,
        onAlbumClick = onAlbumClick,
        onSearchClick = onSearchClick,
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}

// ── Stateless ──
@Composable
fun DescubreScreenContent(
    state: DescubreState,
    onCategoriaClick: (CategoriaUI) -> Unit,
    onGeneroClick: (GeneroUI) -> Unit,
    onAlbumClick: (AlbumDescubreUI) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarDescubre(onSearchClick = onSearchClick, onProfileClick = onProfileClick)

        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                Text(
                    text = "Descubre",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.categorias) { categoria ->
                        CategoriaCard(categoria = categoria, onClick = { onCategoriaClick(categoria) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { SectionHeader(titulo = "Géneros", onVerMasClick = {}) }

            item {
                GeneroGrid(generos = state.generos, onGeneroClick = onGeneroClick)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { SectionHeader(titulo = "Nuevos lanzamientos", onVerMasClick = {}) }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.nuevosLanzamientos) { album ->
                        AlbumCard(album = album, onClick = { onAlbumClick(album) })
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── TopBar ──
@Composable
fun TopBarDescubre(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(bottomEnd = 12.dp))
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_beattreat),
                contentDescription = "Logo BeatTreat",
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(bottomStart = 12.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BeatTreat",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = JaroFont,
                modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

// ── Card de Categoría ──
@Composable
fun CategoriaCard(
    categoria: CategoriaUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(120.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(categoria.colorFondo), Color(categoria.colorFondo).copy(alpha = 0.7f))
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = categoria.nombre,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}

// ── Section Header ──
@Composable
fun SectionHeader(
    titulo: String,
    onVerMasClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = titulo, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onVerMasClick() }
        ) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Ver más", tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Grid de Géneros ──
@Composable
fun GeneroGrid(
    generos: List<GeneroUI>,
    onGeneroClick: (GeneroUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

// ── Chip de Género ──
@Composable
fun GeneroChip(
    genero: GeneroUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(BeatTreatColors.SurfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp)) {
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(genero.colorChip)))
            Text(text = genero.nombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 12.dp))
        }
    }
}

// ── Card de Álbum ──
@Composable
fun AlbumCard(
    album: AlbumDescubreUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        if (album.imagenRes != 0) {
            Image(
                painter = painterResource(id = album.imagenRes),
                contentDescription = album.nombre,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BeatTreatColors.SurfaceVariant)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = album.nombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        Text(text = album.artista, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, maxLines = 1)
    }
}

@Preview(showBackground = true)
@Composable
fun DescubreScreenPreview() {
    BeatTreatTheme { DescubreScreen() }
}