package com.example.beattreat.ui.FeedSiguiendo

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.beattreat.ui.Resena.ResenaDetalladaUI
import com.example.beattreat.ui.theme.BeatTreatColors

// ── Stateful ──────────────────────────────────────────────────────────────────
@Composable
fun FeedSiguiendoScreen(
    onAutorClick: (String) -> Unit = {},
    onResenaClick: (ResenaDetalladaUI) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: FeedSiguiendoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    FeedSiguiendoContent(
        uiState       = uiState,
        onAutorClick  = onAutorClick,
        onResenaClick = onResenaClick,
        onLikeClick   = { viewModel.toggleLike(it) },
        onRetry       = { viewModel.cargarFeed() },
        modifier      = modifier
    )
}

// ── Stateless ─────────────────────────────────────────────────────────────────
@Composable
fun FeedSiguiendoContent(
    uiState: FeedSiguiendoUIState,
    onAutorClick: (String) -> Unit,
    onResenaClick: (ResenaDetalladaUI) -> Unit,
    onLikeClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when {
            // ── Cargando ──
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Cargando feed en tiempo real...",
                        color   = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                }
            }

            // ── No sigue a nadie ──
            uiState.sinSeguidos -> {
                Column(
                    modifier            = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.PersonSearch,
                        contentDescription = null,
                        tint     = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Aún no sigues a nadie",
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sigue usuarios para ver sus reseñas aquí en tiempo real",
                        color    = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            // ── Error ──
            uiState.errorMessage != null && uiState.reviews.isEmpty() -> {
                Column(
                    modifier            = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.WifiOff,
                        contentDescription = null,
                        tint     = BeatTreatColors.Error.copy(alpha = 0.6f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(uiState.errorMessage, color = BeatTreatColors.Error, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors  = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
                    ) { Text("Reintentar", color = Color.White) }
                }
            }

            // ── Lista de reviews en tiempo real ──
            else -> {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        // Indicador de datos en tiempo real
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "En tiempo real · ${uiState.reviews.size} reseñas",
                                color    = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (uiState.reviews.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxWidth().padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.RateReview,
                                        contentDescription = null,
                                        tint     = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "Los usuarios que sigues aún no han publicado reseñas",
                                        color    = Color.White.copy(alpha = 0.4f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.reviews, key = { it.firestoreDocId.ifBlank { it.id.toString() } }) { review ->
                            FeedReviewCard(
                                review      = review,
                                isLiked     = review.firestoreDocId in uiState.likedReviewIds,
                                onLikeClick = { onLikeClick(review.firestoreDocId) },
                                onAutorClick = {
                                    if (review.autorFirestoreUserId.isNotBlank())
                                        onAutorClick(review.autorFirestoreUserId)
                                },
                                onClick     = { onResenaClick(review) }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Card de review en el feed ─────────────────────────────────────────────────
@Composable
fun FeedReviewCard(
    review: ResenaDetalladaUI,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onAutorClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape  = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Fila de autor ──
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clickable { onAutorClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BeatTreatColors.Purple40),
                    contentAlignment = Alignment.Center
                ) {
                    if (review.autorFotoUrl.isNotBlank()) {
                        AsyncImage(
                            model              = review.autorFotoUrl,
                            contentDescription = review.autorNombre,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Filled.AccountCircle, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(review.autorNombre,  color = Color.White,                    fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(review.autorUsuario, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
                Text(review.fecha, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
            }

            Spacer(Modifier.height(12.dp))

            // ── Calificación con estrellas ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        imageVector = if (i < review.calificacion.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null,
                        tint     = if (i < review.calificacion.toInt()) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(review.calificacion.toString(), color = Color.White, fontSize = 13.sp)
            }

            Spacer(Modifier.height(10.dp))

            // ── Texto de la reseña ──
            Text(
                text       = review.texto,
                color      = Color.White.copy(alpha = 0.85f),
                fontSize   = 14.sp,
                lineHeight = 20.sp,
                maxLines   = 4
            )

            Spacer(Modifier.height(12.dp))

            // ── Footer: Like + Comentarios ──
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Like
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint     = if (isLiked) Color(0xFFE91E63) else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = if (isLiked) "${review.likes + 1}" else "${review.likes}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }

                // Comentarios
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.ChatBubbleOutline,
                        contentDescription = null,
                        tint     = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${review.comentarios}",
                        color    = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
