package com.example.login.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.model.ComentarioUI
import com.example.login.model.ResenaData
import com.example.login.model.ResenaDetalladaUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Estado de ComentariosScreen (State Hoisting) ──
data class ComentariosState(
    val resena: ResenaDetalladaUI? = null,
    val comentarios: List<ComentarioUI> = emptyList(),
    val nuevoComentario: String = "",
    val comentariosLikeados: Set<Int> = emptySet()
)

// ── Stateful ──
@Composable
fun ComentariosScreen(
    resenaId: Int,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Busca la reseña por id en los datos locales
    val resena = remember(resenaId) {
        ResenaData.resenasDestacadas.find { it.id == resenaId }
    }

    var state by remember {
        mutableStateOf(
            ComentariosState(
                resena = resena,
                comentarios = ResenaData.comentariosEjemplo
            )
        )
    }

    ComentariosScreenContent(
        state = state,
        onBackClick = onBackClick,
        onNuevoComentarioChange = { state = state.copy(nuevoComentario = it) },
        onEnviarComentario = {
            if (state.nuevoComentario.isNotBlank()) {
                // TODO: guardar en base de datos
                state = state.copy(nuevoComentario = "")
            }
        },
        onLikeComentario = { id ->
            state = if (id in state.comentariosLikeados) {
                state.copy(comentariosLikeados = state.comentariosLikeados - id)
            } else {
                state.copy(comentariosLikeados = state.comentariosLikeados + id)
            }
        },
        modifier = modifier
    )
}

// ── Stateless ──
@Composable
fun ComentariosScreenContent(
    state: ComentariosState,
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
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Reseña original como encabezado ──
            state.resena?.let { resena ->
                item { ResenaEncabezado(resena = resena) }
                item {
                    Text(
                        text = "Comentarios (${state.comentarios.size})",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // ── Lista de comentarios ──
            if (state.comentarios.isEmpty()) {
                item { SinComentarios() }
            } else {
                items(state.comentarios) { comentario ->
                    ComentarioCard(
                        comentario = comentario,
                        isLiked = comentario.id in state.comentariosLikeados,
                        onLikeClick = { onLikeComentario(comentario.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // ── Input nuevo comentario ──
        InputComentario(
            texto = state.nuevoComentario,
            onTextoChange = onNuevoComentarioChange,
            onEnviarClick = onEnviarComentario
        )
    }
}

// ── TopBar ──
@Composable
fun TopBarComentarios(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Comentarios",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
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
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Autor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = resena.autorNombre,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = resena.autorNombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(text = resena.autorUsuario, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Álbum + calificación
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${resena.albumNombre} — ${resena.albumArtista}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ResenaEstrellasCompactas(calificacion = resena.calificacion)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto de la reseña
            Text(
                text = resena.texto,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// ── Estrellas compactas para el encabezado ──
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
                tint = if (index < calificacion.toInt()) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = calificacion.toString(), color = Color.White, fontSize = 12.sp)
    }
}

// ── Mensaje cuando no hay comentarios ──
@Composable
fun SinComentarios(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sé el primero en comentar",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 14.sp
        )
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
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = comentario.autorNombre,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(36.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Burbuja del comentario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(BeatTreatColors.SurfaceVariant)
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = comentario.autorNombre,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = comentario.fecha,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = comentario.autorUsuario,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = comentario.texto,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Like del comentario
            ComentarioLikeRow(
                likes = comentario.likes,
                isLiked = isLiked,
                onLikeClick = onLikeClick
            )
        }
    }
}

// ── Fila de like extraída ──
@Composable
fun ComentarioLikeRow(
    likes: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(start = 4.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Like",
                tint = if (isLiked) Color.Red else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = if (isLiked) "${likes + 1}" else "$likes",
            color = Color.White.copy(alpha = 0.6f),
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
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = "Yo",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = texto,
            onValueChange = onTextoChange,
            placeholder = { Text("Agregar un comentario...", color = BeatTreatColors.TextGray) },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            colors = TextFieldDefaults.colors(
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
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (texto.isNotBlank()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onEnviarClick,
                enabled = texto.isNotBlank()
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Enviar comentario",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComentariosScreenPreview() {
    BeatTreatTheme {
        ComentariosScreen(resenaId = 1)
    }
}