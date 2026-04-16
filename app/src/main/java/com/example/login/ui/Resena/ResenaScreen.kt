package com.example.login.ui.Resena

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Stateful ──────────────────────────────────────────────────────────────────
@Composable
fun ResenaScreen(
    albumId: Int,
    onBackClick: () -> Unit = {},
    onResenaClick: (ResenaDetalladaUI) -> Unit = {},
    onEscribirResenaClick: () -> Unit = {},
    /**
     * Navega al perfil del autor de la reseña.
     * Recibe el autorUserId real del backend.
     */
    onAutorClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ResenaViewModel
) {
    LaunchedEffect(albumId) {
        viewModel.cargarResenas(albumId)
    }

    val uiState by viewModel.uiState.collectAsState()

    ResenaScreenContent(
        uiState               = uiState,
        onBackClick           = onBackClick,
        onResenaClick         = onResenaClick,
        onLikeClick           = { viewModel.toggleLikeResena(it) },
        onEscribirResenaClick = onEscribirResenaClick,
        onAutorClick          = onAutorClick,
        modifier              = modifier
    )
}

// ── Stateless ─────────────────────────────────────────────────────────────────
@Composable
fun ResenaScreenContent(
    uiState: ResenaUIState,
    onBackClick: () -> Unit,
    onResenaClick: (ResenaDetalladaUI) -> Unit,
    onLikeClick: (Int) -> Unit,
    onEscribirResenaClick: () -> Unit,
    onAutorClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarResena(
            onBackClick     = onBackClick,
            onEscribirClick = onEscribirResenaClick
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    text       = if (uiState.albumNombre.isNotBlank()) uiState.albumNombre else "Reseñas",
                    color      = Color.White,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }

            if (uiState.resenas.isEmpty()) {
                item { SinResenasAun(onEscribirClick = onEscribirResenaClick) }
            } else {
                items(uiState.resenas) { resena ->
                    ResenaDetalladaCard(
                        resena      = resena,
                        isLiked     = resena.id in uiState.resenasLikeadas,
                        onClick     = { onResenaClick(resena) },
                        onLikeClick = { onLikeClick(resena.id) },
                        // ← Usar el autorUserId real, no un proxy
                        onAutorClick = {
                            if (resena.autorUserId > 0) onAutorClick(resena.autorUserId)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── Estado vacío ──────────────────────────────────────────────────────────────
@Composable
fun SinResenasAun(onEscribirClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.fillMaxWidth().padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector        = Icons.Filled.MusicNote,
            contentDescription = null,
            tint               = Color.White.copy(alpha = 0.25f),
            modifier           = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text     = "Aún no hay reseñas para este álbum",
            color    = Color.White.copy(alpha = 0.5f),
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text       = "¡Sé el primero en opinar!",
            color      = BeatTreatColors.Purple60,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.clickable { onEscribirClick() }
        )
    }
}

// ── TopBar ────────────────────────────────────────────────────────────────────
@Composable
fun TopBarResena(
    onBackClick: () -> Unit,
    onEscribirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier         = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(bottomEnd = 12.dp))
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter            = painterResource(id = R.drawable.logo_beattreat),
                contentDescription = "Logo BeatTreat",
                modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                contentScale       = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 12.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint               = Color.White,
                    modifier           = Modifier.size(26.dp)
                )
            }
            Text(
                text       = "BeatTreat",
                color      = Color.White,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = JaroFont,
                modifier   = Modifier.weight(1f).padding(start = 4.dp)
            )
            Text(
                text       = "Escribir",
                color      = Color.White,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier   = Modifier.clickable { onEscribirClick() }
            )
        }
    }
}

// ── Card de Reseña Detallada ──────────────────────────────────────────────────
@Composable
fun ResenaDetalladaCard(
    resena: ResenaDetalladaUI,
    isLiked: Boolean,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onAutorClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            ResenaAutorRow(resena = resena, onAutorClick = onAutorClick)
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

// ── Fila de autor con botón "Ver perfil" ──────────────────────────────────────
@Composable
fun ResenaAutorRow(
    resena: ResenaDetalladaUI,
    onAutorClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier
                .weight(1f)
                .clickable { onAutorClick() }
        ) {
            if (resena.autorFotoRes != 0) {
                Image(
                    painter            = painterResource(id = resena.autorFotoRes),
                    contentDescription = resena.autorNombre,
                    modifier           = Modifier.size(42.dp).clip(CircleShape),
                    contentScale       = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = resena.autorNombre,
                    tint               = Color.White,
                    modifier           = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text       = resena.autorNombre,
                    color      = Color.White,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text     = resena.autorUsuario,
                    color    = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        // Botón "Ver perfil" solo si hay un userId válido
        if (resena.autorUserId > 0) {
            TextButton(onClick = onAutorClick) {
                Text(
                    text     = "Ver perfil",
                    color    = BeatTreatColors.Purple60,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ── Fila de álbum ─────────────────────────────────────────────────────────────
@Composable
fun ResenaAlbumRow(resena: ResenaDetalladaUI, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (resena.albumRes != 0) {
            Image(
                painter            = painterResource(id = resena.albumRes),
                contentDescription = resena.albumNombre,
                modifier           = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale       = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BeatTreatColors.Purple40)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = resena.albumNombre,  color = Color.White,                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = resena.albumArtista, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}

// ── Estrellas ─────────────────────────────────────────────────────────────────
@Composable
fun ResenaEstrellas(calificacion: Float, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Icon(
                imageVector        = Icons.Filled.Star,
                contentDescription = null,
                tint               = if (index < calificacion) Color(0xFFFFC107) else Color.Gray,
                modifier           = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = calificacion.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Footer ────────────────────────────────────────────────────────────────────
@Composable
fun ResenaFooter(
    resena: ResenaDetalladaUI,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector        = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint               = if (isLiked) Color.Red else Color.White,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Text(text = resena.likes.toString(), color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector        = Icons.Filled.MusicNote,
                contentDescription = "Comentarios",
                tint               = Color.White.copy(alpha = 0.6f),
                modifier           = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${resena.comentarios}", color = Color.White, fontSize = 14.sp)
        }
        Text(text = resena.fecha, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun ResenaScreenPreview() {
    BeatTreatTheme {
        ResenaScreenContent(
            uiState               = ResenaUIState(),
            onBackClick           = {},
            onResenaClick         = {},
            onLikeClick           = {},
            onEscribirResenaClick = {},
            onAutorClick          = {}
        )
    }
}