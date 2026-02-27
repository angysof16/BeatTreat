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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.login.model.ArtistaHomeUI
import com.example.login.model.HomeData
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Estado de HomeScreen (State Hoisting) ──
data class HomeState(
    val bannerActual: Int = 0,
    val artistas: List<ArtistaHomeUI> = HomeData.artistas
)

// ── Stateful ──
@Composable
fun HomeScreen(
    onAlbumClick: (Int) -> Unit = {},
    onArtistaClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var bannerIndex by remember { mutableIntStateOf(0) }
    val homeState = remember { HomeState() }

    HomeScreenContent(
        state = homeState,
        bannerIndex = bannerIndex,
        onBannerChange = { bannerIndex = it },
        onAlbumClick = onAlbumClick,
        onArtistaClick = onArtistaClick,
        onSearchClick = onSearchClick,
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}

// ── Stateless ──
@Composable
fun HomeScreenContent(
    state: HomeState,
    bannerIndex: Int,
    onBannerChange: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    onArtistaClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarHome(onSearchClick = onSearchClick, onProfileClick = onProfileClick)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Banner(
                    bannerIndex = bannerIndex,
                    onPrevious = { if (bannerIndex > 0) onBannerChange(bannerIndex - 1) },
                    onNext = { onBannerChange(bannerIndex + 1) }
                )
            }

            items(state.artistas) { artista ->
                ArtistaSection(
                    artista = artista,
                    onAlbumClick = onAlbumClick,
                    onArtistaClick = { onArtistaClick(artista.id) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── TopBar ──
@Composable
fun TopBarHome(
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

// ── Banner ──
@Composable
fun Banner(
    bannerIndex: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.banner),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Tu mejor ritmo \n todos los días", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }
        BannerControles(onPrevious = onPrevious, onNext = onNext)
        BannerIndicadores()
    }
}

// ── Flechas del Banner ──
@Composable
fun BannerControles(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevious) {
            Image(painter = painterResource(R.drawable.flecha_izquierda), contentDescription = "Anterior", modifier = Modifier.size(28.dp))
        }
        IconButton(onClick = onNext) {
            Image(painter = painterResource(R.drawable.flecha_derecha), contentDescription = "Siguiente", modifier = Modifier.size(28.dp))
        }
    }
}

// ── Indicadores del Banner ──
@Composable
fun BannerIndicadores(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(bottom = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(R.drawable.puntos), contentDescription = null, modifier = Modifier.size(40.dp))
    }
}

// ── Sección de Artista ──
@Composable
fun ArtistaSection(
    artista: ArtistaHomeUI,
    onAlbumClick: (Int) -> Unit,
    onArtistaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onArtistaClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = artista.nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ArrowForward, contentDescription = "Ver más", tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(30.dp)) {
            artista.albumes.take(3).forEach { album ->
                AlbumItem(album = album, onClick = { onAlbumClick(album.id) })
            }
        }
    }
}

// ── Item de Álbum ──
@Composable
fun AlbumItem(
    album: com.example.login.model.AlbumHomeUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BeatTreatColors.SurfaceVariant)
        ) {
            if (album.imagenRes != 0) {
                Image(
                    painter = painterResource(id = album.imagenRes),
                    contentDescription = album.nombre,
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        AlbumItemFooter(nombre = album.nombre)
    }
}

// ── Footer del AlbumItem ──
@Composable
fun AlbumItemFooter(
    nombre: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.width(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = nombre, color = Color.White, fontSize = 14.sp, maxLines = 1, modifier = Modifier.weight(1f))
        IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BeatTreatTheme { HomeScreen() }
}