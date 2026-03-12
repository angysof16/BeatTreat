package com.example.login.Comentarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.login.Resena.ComentarioUI
import com.example.login.Resena.ResenaDetalladaUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Stateful ──
@Composable
fun ComentariosScreen(
    resenaId: Int,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ComentariosViewModel
) {
    LaunchedEffect(resenaId) {
        viewModel.cargarComentarios(resenaId)
    }

    val uiState by viewModel.uiState.collectAsState()

    ComentariosScreenContent(
        uiState                 = uiState,
        onBackClick             = onBackClick,
        onNuevoComentarioChange = { viewModel.onNuevoComentarioChange(it) },
        onEnviarComentario      = { viewModel.enviarComentario() },
        onLikeComentario        = { viewModel.toggleLikeComentario(it) },
        modifier                = modifier
    )
}

// ── Stateless ──
@Composable
fun ComentariosScreenContent(
    uiState: ComentariosUIState,
    onBackClick: () -> Unit,
    onNuevoComentarioChange: (String) -> Unit,
    onEnviarComentario: () -> Unit,
    onLikeComentario: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarComentarios(onBackClick = onBackClick)

        LazyColumn(
            modifier       = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.resena?.let { resena ->
                item { ResenaEncabezado(resena = resena) }
                item {
                    Text(
                        text       = "Comentarios (${uiState.comentarios.size})",
                        color      = Color.White,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            if (uiState.comentarios.isEmpty()) {
                item { SinComentarios() }
            } else {
                items(uiState.comentarios) { comentario ->
                    ComentarioCard(
                        comentario  = comentario,
                        isLiked     = comentario.id in uiState.comentariosLikeados,
                        onLikeClick = { onLikeComentario(comentario.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        InputComentario(
            texto         = uiState.nuevoComentario,
            onTextoChange = onNuevoComentarioChange,
            onEnviarClick = onEnviarComentario
        )
    }
}

// ── TopBar consistente con el resto de la app ──
@Composable
fun TopBarComentarios(
    onBackClick: () -> Unit,
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
            verticalAlignment = Alignment.CenterVertically
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
                text     = "Comentarios",
                color    = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

// ── Encabezado: la reseña original resumida ──
@Composable
fun ResenaEncabezado(
    resena: ResenaDetalladaUI,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AccountCircle, contentDescription = resena.autorNombre, tint = Color.White, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = resena.autorNombre,  color = Color.White,                    fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(text = resena.autorUsuario, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = "${resena.albumNombre} — ${resena.albumArtista}",
                    color      = Color.White.copy(alpha = 0.8f),
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ResenaEstrellasCompactas(calificacion = resena.calificacion)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = resena.texto, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

// ── Estrellas compactas ──
@Composable
fun ResenaEstrellasCompactas(
    calificacion: Float,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint     = if (index < calificacion.toInt()) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = calificacion.toString(), color = Color.White, fontSize = 12.sp)
    }
}

// ── Sin comentarios ──
@Composable
fun SinComentarios(modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier.fillMaxWidth().padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Sé el primero en comentar", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
    }
}

// ── Card de Comentario ──
@Composable
fun ComentarioCard(
    comentario: ComentarioUI,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(Icons.Filled.AccountCircle, contentDescription = comentario.autorNombre, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(BeatTreatColors.SurfaceVariant)
                    .padding(12.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = comentario.autorNombre, color = Color.White,                    fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = comentario.fecha,       color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                    Text(text = comentario.autorUsuario, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = comentario.texto, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
            ComentarioLikeRow(likes = comentario.likes, isLiked = isLiked, onLikeClick = onLikeClick)
        }
    }
}

// ── Like del comentario ──
@Composable
fun ComentarioLikeRow(
    likes: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(start = 4.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onLikeClick, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector        = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Like",
                tint               = if (isLiked) Color.Red else Color.White.copy(alpha = 0.6f),
                modifier           = Modifier.size(16.dp)
            )
        }
        Text(
            text     = if (isLiked) "${likes + 1}" else "$likes",
            color    = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}

// ── Input nuevo comentario ──
@Composable
fun InputComentario(
    texto: String,
    onTextoChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.AccountCircle, contentDescription = "Yo", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value         = texto,
            onValueChange = onTextoChange,
            placeholder   = { Text("Agregar un comentario...", color = BeatTreatColors.TextGray) },
            modifier      = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)),
            colors        = TextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
                cursorColor             = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier         = Modifier.size(44.dp).clip(CircleShape)
                .background(if (texto.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onEnviarClick, enabled = texto.isNotBlank()) {
                Icon(Icons.Filled.Send, contentDescription = "Enviar comentario", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComentariosScreenPreview() {
    BeatTreatTheme {
        ComentariosScreenContent(
            uiState                 = ComentariosUIState(),
            onBackClick             = {},
            onNuevoComentarioChange = {},
            onEnviarComentario      = {},
            onLikeComentario        = {}
        )
    }
}