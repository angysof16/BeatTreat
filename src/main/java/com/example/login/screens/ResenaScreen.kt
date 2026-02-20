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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.model.ComentarioUI
import com.example.login.model.ResenaData
import com.example.login.model.ResenaDetalladaUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Estado de ResenaScreen (State Hoisting) ──
data class ResenaScreenState(
    val resenas: List<ResenaDetalladaUI> = ResenaData.resenasDestacadas,
    val comentarios: List<ComentarioUI> = ResenaData.comentariosEjemplo,
    val resenasLikeadas: Set<Int> = emptySet()
)

// ── Stateful ──
@Composable
fun ResenaScreen(
    onBackClick: () -> Unit = {},
    onResenaClick: (ResenaDetalladaUI) -> Unit = {},
    onEscribirResenaClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(ResenaScreenState()) }

    ResenaScreenContent(
        state = state,
        onBackClick = onBackClick,
        onResenaClick = onResenaClick,
        onLikeClick = { resenaId ->
            state = if (resenaId in state.resenasLikeadas) {
                state.copy(resenasLikeadas = state.resenasLikeadas - resenaId)
            } else {
                state.copy(resenasLikeadas = state.resenasLikeadas + resenaId)
            }
        },
        onEscribirResenaClick = onEscribirResenaClick,
        modifier = modifier
    )
}

// ── Stateless ──
@Composable
fun ResenaScreenContent(
    state: ResenaScreenState,
    onBackClick: () -> Unit,
    onResenaClick: (ResenaDetalladaUI) -> Unit,
    onLikeClick: (Int) -> Unit,
    onEscribirResenaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarResena(onBackClick = onBackClick, onEscribirClick = onEscribirResenaClick)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    text = "Reseñas destacadas",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
            items(state.resenas) { resena ->
                ResenaDetalladaCard(
                    resena = resena,
                    isLiked = resena.id in state.resenasLikeadas,
                    onClick = { onResenaClick(resena) },
                    onLikeClick = { onLikeClick(resena.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── TopBar ──
@Composable
fun TopBarResena(
    onBackClick: () -> Unit,
    onEscribirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Text(text = "Reseñas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "Escribir", color = Color.White, fontSize = 16.sp, modifier = Modifier.clickable { onEscribirClick() })
    }
}

// ── Card de Reseña Detallada ──
@Composable
fun ResenaDetalladaCard(
    resena: ResenaDetalladaUI,
    isLiked: Boolean,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            ResenaAutorRow(resena = resena)
            Spacer(modifier = Modifier.height(12.dp))
            ResenaAlbumRow(resena = resena)
            Spacer(modifier = Modifier.height(12.dp))
            ResenaEstrellas(calificacion = resena.calificacion)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = resena.texto, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            ResenaFooter(resena = resena, isLiked = isLiked, onLikeClick = onLikeClick)
        }
    }
}

// ── Fila de autor ──
@Composable
fun ResenaAutorRow(
    resena: ResenaDetalladaUI,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (resena.autorFotoRes != 0) {
                Image(
                    painter = painterResource(id = resena.autorFotoRes),
                    contentDescription = resena.autorNombre,
                    modifier = Modifier.size(42.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Filled.AccountCircle, contentDescription = resena.autorNombre, tint = Color.White, modifier = Modifier.size(42.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = resena.autorNombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = resena.autorUsuario, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White)
        }
    }
}

// ── Fila de álbum  ──
@Composable
fun ResenaAlbumRow(
    resena: ResenaDetalladaUI,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (resena.albumRes != 0) {
            Image(
                painter = painterResource(id = resena.albumRes),
                contentDescription = resena.albumNombre,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(BeatTreatColors.Purple40))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = resena.albumNombre, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = resena.albumArtista, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}

// ── Estrellas de calificación ──
@Composable
fun ResenaEstrellas(
    calificacion: Float,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < calificacion) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = calificacion.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Footer de la card ──
@Composable
fun ResenaFooter(
    resena: ResenaDetalladaUI,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(text = resena.likes.toString(), color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = " ${resena.comentarios}", color = Color.White, fontSize = 14.sp)
        }
        Text(text = resena.fecha, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ResenaScreenPreview() {
    BeatTreatTheme { ResenaScreen() }
}