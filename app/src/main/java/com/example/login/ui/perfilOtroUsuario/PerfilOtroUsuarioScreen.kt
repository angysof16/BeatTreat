package com.example.login.ui.PerfilOtroUsuario

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──────────────────────────────────────────────────────────────────
@Composable
fun PerfilOtroUsuarioScreen(
    userId: Int,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PerfilOtroUsuarioViewModel
) {
    LaunchedEffect(userId) {
        viewModel.cargarPerfil(userId)
    }

    val uiState by viewModel.uiState.collectAsState()

    PerfilOtroUsuarioScreenContent(
        uiState     = uiState,
        onBackClick = onBackClick,
        modifier    = modifier
    )
}

// ── Stateless ─────────────────────────────────────────────────────────────────
@Composable
fun PerfilOtroUsuarioScreenContent(
    uiState: PerfilOtroUsuarioUIState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── TopBar ──
        TopBarOtroUsuario(onBackClick = onBackClick)

        when {
            // Estado de carga
            uiState.isLoading -> {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                }
            }

            // Error al cargar perfil (sin usuario)
            uiState.usuario == null && uiState.errorMessage != null -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector        = Icons.Filled.PersonOff,
                            contentDescription = null,
                            tint               = Color.White.copy(alpha = 0.3f),
                            modifier           = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text     = uiState.errorMessage,
                            color    = BeatTreatColors.Error,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onBackClick,
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = BeatTreatColors.Purple60
                            )
                        ) {
                            Text("Volver", color = Color.White)
                        }
                    }
                }
            }

            // Contenido normal
            uiState.usuario != null -> {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // ── Header con avatar y datos básicos ──
                    item {
                        HeaderOtroUsuario(usuario = uiState.usuario)
                    }

                    // ── Error al cargar reviews (pero perfil OK) ──
                    if (uiState.errorMessage != null) {
                        item {
                            Text(
                                text     = uiState.errorMessage,
                                color    = BeatTreatColors.Error,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // ── Título sección reviews ──
                    item {
                        Text(
                            text       = "Reviews (${uiState.reviews.size})",
                            color      = Color.White,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(
                                horizontal = 20.dp,
                                vertical   = 12.dp
                            )
                        )
                    }

                    // ── Lista de reviews ──
                    if (uiState.reviews.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text     = "Este usuario aún no ha escrito reviews",
                                    color    = Color.White.copy(alpha = 0.4f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(uiState.reviews) { review ->
                            ReviewOtroUsuarioCard(review = review)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── TopBar ────────────────────────────────────────────────────────────────────
@Composable
fun TopBarOtroUsuario(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector        = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint               = Color.White,
                modifier           = Modifier.size(26.dp)
            )
        }
        Text(
            text       = "Perfil",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(start = 4.dp)
        )
    }
}

// ── Header del perfil ─────────────────────────────────────────────────────────
@Composable
fun HeaderOtroUsuario(
    usuario: OtroUsuarioUI,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BeatTreatColors.PurpleDark, MaterialTheme.colorScheme.background)
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Avatar ──
        Box(
            modifier         = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(BeatTreatColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (usuario.fotoPerfilUrl.isNotBlank()) {
                SubcomposeAsyncImage(
                    model              = usuario.fotoPerfilUrl,
                    contentDescription = usuario.nombre,
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator(
                                color       = BeatTreatColors.Purple60,
                                modifier    = Modifier.size(32.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        is AsyncImagePainter.State.Error -> {
                            Icon(
                                imageVector        = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                tint               = Color.White.copy(alpha = 0.6f),
                                modifier           = Modifier.size(80.dp)
                            )
                        }
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            } else {
                Icon(
                    imageVector        = Icons.Filled.AccountCircle,
                    contentDescription = usuario.nombre,
                    tint               = Color.White.copy(alpha = 0.6f),
                    modifier           = Modifier.size(80.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Nombre ──
        Text(
            text       = usuario.nombre,
            color      = Color.White,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold
        )

        // ── Username ──
        Text(
            text     = usuario.username,
            color    = BeatTreatColors.Purple60,
            fontSize = 14.sp
        )

        // ── Bio ──
        if (usuario.bio.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text      = usuario.bio,
                color     = Color.White.copy(alpha = 0.7f),
                fontSize  = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// ── Card de un review ─────────────────────────────────────────────────────────
@Composable
fun ReviewOtroUsuarioCard(
    review: ReviewOtroUsuarioUI,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

            // ── Álbum ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BeatTreatColors.PurpleDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Album,
                        contentDescription = null,
                        tint               = Color.White.copy(alpha = 0.6f),
                        modifier           = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text       = review.albumNombre,
                        color      = Color.White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text     = review.albumArtista,
                        color    = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Estrellas ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        imageVector        = Icons.Filled.Star,
                        contentDescription = null,
                        tint               = if (index < review.rating) Color(0xFFFFC107)
                                            else Color.Gray,
                        modifier           = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text     = review.rating.toString(),
                    color    = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text     = review.fecha,
                    color    = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Contenido ──
            Text(
                text       = review.contenido,
                color      = Color.White.copy(alpha = 0.85f),
                fontSize   = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun PerfilOtroUsuarioScreenPreview() {
    BeatTreatTheme {
        PerfilOtroUsuarioScreenContent(
            uiState = PerfilOtroUsuarioUIState(
                usuario = OtroUsuarioUI(
                    id            = 2,
                    nombre        = "María García",
                    username      = "@mariagrck",
                    bio           = "Reggaeton fan | Bad Bunny forever",
                    fotoPerfilUrl = ""
                ),
                reviews = listOf(
                    ReviewOtroUsuarioUI(
                        id           = 1,
                        albumNombre  = "Un Verano Sin Ti",
                        albumArtista = "Bad Bunny",
                        rating       = 5.0f,
                        contenido    = "El mejor álbum del año sin duda.",
                        fecha        = "2024-01-15"
                    )
                )
            ),
            onBackClick = {}
        )
    }
}
