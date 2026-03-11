package com.example.login.AlbumDetalle

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun AlbumDetalleScreen(
    albumId: Int,
    onBackClick: () -> Unit = {},
    onVerResenasClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlbumDetalleViewModel
) {
    LaunchedEffect(albumId) {
        viewModel.cargarAlbum(albumId)
    }

    val uiState by viewModel.uiState.collectAsState()

    AlbumDetalleScreenContent(
        uiState           = uiState,
        onBackClick       = onBackClick,
        onVerResenasClick = onVerResenasClick,
        onFavoritoClick   = { viewModel.toggleFavorito() },
        modifier          = modifier
    )
}

// ── Stateless ──
@Composable
fun AlbumDetalleScreenContent(
    uiState: AlbumDetalleUIState,
    onBackClick: () -> Unit,
    onVerResenasClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.album == null) {
        AlbumNoEncontrado(onBackClick = onBackClick, modifier = modifier)
        return
    }

    val album = uiState.album

    LazyColumn(
        modifier       = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            AlbumPortadaHeader(
                album           = album,
                esFavorito      = uiState.esFavorito,
                onBackClick     = onBackClick,
                onFavoritoClick = onFavoritoClick
            )
        }
        item { AlbumInfoSection(album = album) }
        item {
            BotonVerResenas(
                totalResenas = album.totalResenas,
                calificacion = album.calificacionPromedio,
                onClick      = onVerResenasClick
            )
        }
        // ── Lista de canciones ──
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text       = "Canciones",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(album.canciones.size) { index ->
            CancionItem(
                cancion    = album.canciones[index],
                esUltima   = index == album.canciones.lastIndex
            )
        }
    }
}

// ── Fila de una canción ──
@Composable
fun CancionItem(
    cancion: CancionDetalleUI,
    esUltima: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número de canción
            Box(
                modifier         = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BeatTreatColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text      = cancion.numero.toString(),
                    color     = Color.White.copy(alpha = 0.7f),
                    fontSize  = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            // Título
            Text(
                text       = cancion.titulo,
                color      = Color.White,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Normal,
                modifier   = Modifier.weight(1f)
            )
            // Duración
            Text(
                text     = cancion.duracion,
                color    = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
        }
        if (!esUltima) {
            Divider(
                color    = Color.White.copy(alpha = 0.07f),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

// ── Header: portada con gradiente ──
@Composable
fun AlbumPortadaHeader(
    album: AlbumDetalleUI,
    esFavorito: Boolean,
    onBackClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().height(380.dp)) {
        if (album.imagenRes != 0) {
            Image(
                painter            = painterResource(id = album.imagenRes),
                contentDescription = album.nombre,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
        } else {
            Box(
                modifier         = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(colors = listOf(BeatTreatColors.Purple60, Color(0xFF1A1A1A)))
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.3f),
                    modifier           = Modifier.size(100.dp)
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Black.copy(alpha = 0.4f),
                        0.55f to Color.Transparent,
                        1.0f to Color(0xFF121212)
                    )
                )
            )
        )

        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp).align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Box(
                    modifier         = Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
            IconButton(onClick = onFavoritoClick) {
                Box(
                    modifier         = Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint               = if (esFavorito) Color.Red else Color.White,
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }
        }

        if (album.imagenRes != 0) {
            Image(
                painter            = painterResource(id = album.imagenRes),
                contentDescription = album.nombre,
                modifier           = Modifier.size(180.dp).align(Alignment.Center).clip(RoundedCornerShape(16.dp)),
                contentScale       = ContentScale.Crop
            )
        }
    }
}

// ── Sección de información ──
@Composable
fun AlbumInfoSection(album: AlbumDetalleUI, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(text = album.nombre,  color = Color.White,               fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = album.artista, color = BeatTreatColors.Purple60,  fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipInfo(texto = album.año)
            ChipInfo(texto = album.genero)
            ChipInfo(texto = album.duracionTotal)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = album.descripcion, color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        AlbumCalificacionRow(calificacion = album.calificacionPromedio, totalResenas = album.totalResenas)
    }
}

// ── Chip de metadato ──
@Composable
fun ChipInfo(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).background(BeatTreatColors.SurfaceVariant).padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text = texto, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

// ── Calificación con estrellas ──
@Composable
fun AlbumCalificacionRow(calificacion: Float, totalResenas: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = calificacion.toString(), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "$totalResenas reseñas", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            EstrellasCalificacion(calificacion = calificacion)
        }
    }
}

// ── Estrellas con media estrella ──
@Composable
fun EstrellasCalificacion(calificacion: Float, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val entero     = calificacion.toInt()
        val tieneMedia = (calificacion - entero) >= 0.5f
        repeat(5) { index ->
            val icono = when {
                index < entero                -> Icons.Filled.Star
                index == entero && tieneMedia -> Icons.Filled.StarHalf
                else                          -> Icons.Filled.StarBorder
            }
            Icon(
                imageVector        = icono,
                contentDescription = null,
                tint               = if (index < entero || (index == entero && tieneMedia)) Color(0xFFFFC107) else Color.Gray,
                modifier           = Modifier.size(28.dp)
            )
        }
    }
}

// ── Botón ver reseñas ──
@Composable
fun BotonVerResenas(totalResenas: Int, calificacion: Float, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(colors = listOf(BeatTreatColors.Purple60, Color(0xFF8B5CF6))))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Ver reseñas",                        color = Color.White,                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "$totalResenas opiniones de usuarios", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
        Box(
            modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = calificacion.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Álbum no encontrado ──
@Composable
fun AlbumNoEncontrado(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Álbum no encontrado", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)) {
                Text("Volver", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumDetalleScreenPreview() {
    BeatTreatTheme {
        AlbumDetalleScreenContent(
            uiState           = AlbumDetalleUIState(),
            onBackClick       = {},
            onVerResenasClick = {},
            onFavoritoClick   = {}
        )
    }
}