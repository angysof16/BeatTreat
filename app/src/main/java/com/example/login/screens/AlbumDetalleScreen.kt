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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.data.AlbumDetalleData
import com.example.login.model.AlbumDetalleUI
import com.example.login.model.CancionDetalleUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Estado de AlbumDetalleScreen ──
data class AlbumDetalleState(
    val album: AlbumDetalleUI? = null,
    val esFavorito: Boolean = false
)

// ── Stateful ──
@Composable
fun AlbumDetalleScreen(
    albumId: Int,
    onBackClick: () -> Unit = {},
    onVerResenasClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val album: AlbumDetalleUI? = remember(albumId) { AlbumDetalleData.findById(albumId) }

    var state by remember {
        mutableStateOf(AlbumDetalleState(album = album))
    }

    AlbumDetalleScreenContent(
        state = state,
        onBackClick = onBackClick,
        onVerResenasClick = onVerResenasClick,
        onFavoritoClick = { state = state.copy(esFavorito = !state.esFavorito) },
        modifier = modifier
    )
}

// ── Stateless ──
@Composable
fun AlbumDetalleScreenContent(
    state: AlbumDetalleState,
    onBackClick: () -> Unit,
    onVerResenasClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.album == null) {
        AlbumNoEncontrado(onBackClick = onBackClick, modifier = modifier)
        return
    }

    val album = state.album

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {

        // ── Portada con gradiente + controles ──
        item {
            AlbumPortadaHeader(
                album = album,
                esFavorito = state.esFavorito,
                onBackClick = onBackClick,
                onFavoritoClick = onFavoritoClick
            )
        }

        // ── Nombre, artista y estadísticas ──
        item {
            AlbumInfoSection(album = album)
        }

        // ── Botón ver reseñas ──
        item {
            BotonVerResenas(
                totalResenas = album.totalResenas,
                calificacion = album.calificacionPromedio,
                onClick = onVerResenasClick
            )
        }


    }
}

// ── Header: portada con gradiente oscuro superpuesto ──
@Composable
fun AlbumPortadaHeader(
    album: AlbumDetalleUI,
    esFavorito: Boolean,
    onBackClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
    ) {
        // Imagen de fondo
        if (album.imagenRes != 0) {
            Image(
                painter = painterResource(id = album.imagenRes),
                contentDescription = album.nombre,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BeatTreatColors.Purple60, Color(0xFF1A1A1A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        // Gradiente de transición hacia el fondo oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.4f),
                            0.55f to Color.Transparent,
                            1.0f to Color(0xFF121212)
                        )
                    )
                )
        )

        // Barra superior: Atrás + Favorito
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            IconButton(onClick = onFavoritoClick) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (esFavorito) Color.Red else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Portada cuadrada centrada
        if (album.imagenRes != 0) {
            Image(
                painter = painterResource(id = album.imagenRes),
                contentDescription = album.nombre,
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ── Sección de información: nombre, artista, meta-datos ──
@Composable
fun AlbumInfoSection(
    album: AlbumDetalleUI,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Nombre del álbum
        Text(
            text = album.nombre,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Artista
        Text(
            text = album.artista,
            color = BeatTreatColors.Purple60,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Chips de año y género
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipInfo(texto = album.año)
            ChipInfo(texto = album.genero)
            ChipInfo(texto = album.duracionTotal)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = album.descripcion,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calificación promedio
        AlbumCalificacionRow(
            calificacion = album.calificacionPromedio,
            totalResenas = album.totalResenas
        )
    }
}

// ── Chip pequeño de metadato ──
@Composable
fun ChipInfo(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BeatTreatColors.SurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text = texto, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

// ── Fila de calificación con estrellas ──
@Composable
fun AlbumCalificacionRow(
    calificacion: Float,
    totalResenas: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = calificacion.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$totalResenas reseñas",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            EstrellasCalificacion(calificacion = calificacion)
        }
    }
}

// ── Estrellas con soporte para media estrella ──
@Composable
fun EstrellasCalificacion(
    calificacion: Float,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val entero = calificacion.toInt()
        val tieneMedia = (calificacion - entero) >= 0.5f

        repeat(5) { index ->
            val icono = when {
                index < entero -> Icons.Filled.Star
                index == entero && tieneMedia -> Icons.Filled.StarHalf
                else -> Icons.Filled.StarBorder
            }
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (index < entero || (index == entero && tieneMedia))
                    Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ── Botón para ver las reseñas ──
@Composable
fun BotonVerResenas(
    totalResenas: Int,
    calificacion: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BeatTreatColors.Purple60, Color(0xFF8B5CF6))
                )
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.ChatBubbleOutline,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Ver reseñas",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$totalResenas opiniones de usuarios",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }

        // Calificación compacta
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = calificacion.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Pantalla de álbum no encontrado ──
@Composable
fun AlbumNoEncontrado(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Álbum no encontrado", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
            ) {
                Text("Volver", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumDetalleScreenPreview() {
    BeatTreatTheme {
        AlbumDetalleScreen(albumId = 5)
    }
}