package com.example.login.ui.PerfilOtroUsuario

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

@Composable
fun PerfilOtroUsuarioScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PerfilOtroUsuarioViewModel
) {
    LaunchedEffect(userId) { viewModel.cargarPerfil(userId) }
    val uiState by viewModel.uiState.collectAsState()
    PerfilOtroUsuarioScreenContent(
        uiState       = uiState,
        onBackClick   = onBackClick,
        onFollowClick = { viewModel.toggleFollow() },
        modifier      = modifier
    )
}

@Composable
fun PerfilOtroUsuarioScreenContent(
    uiState: PerfilOtroUsuarioUIState,
    onBackClick: () -> Unit,
    onFollowClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarOtroUsuario(onBackClick = onBackClick)

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                }
            }

            uiState.usuario == null && uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.PersonOff, null,
                            tint     = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(uiState.errorMessage, color = BeatTreatColors.Error, fontSize = 15.sp)
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = onBackClick,
                            colors  = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
                        ) { Text("Volver", color = Color.White) }
                    }
                }
            }

            uiState.usuario != null -> {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        HeaderOtroUsuario(
                            usuario        = uiState.usuario,
                            isFollowing    = uiState.isFollowing,
                            puedeFollow    = uiState.puedeFollow,
                            isFollowLoading = uiState.isFollowLoading,
                            onFollowClick  = onFollowClick
                        )
                    }

                    if (uiState.errorMessage != null) {
                        item {
                            Text(
                                uiState.errorMessage,
                                color    = BeatTreatColors.Error,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                        }
                    }

                    item {
                        Text(
                            "Reviews (${uiState.reviews.size})",
                            color      = Color.White,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }

                    if (uiState.reviews.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Este usuario aún no ha escrito reviews",
                                    color    = Color.White.copy(alpha = 0.4f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(uiState.reviews) { review ->
                            ReviewOtroUsuarioCard(review = review)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarOtroUsuario(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Text(
            "Perfil", color = Color.White, fontSize = 20.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun HeaderOtroUsuario(
    usuario: OtroUsuarioUI,
    isFollowing: Boolean,
    puedeFollow: Boolean,
    isFollowLoading: Boolean,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BeatTreatColors.PurpleDark, MaterialTheme.colorScheme.background)
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier.size(90.dp).clip(CircleShape).background(BeatTreatColors.SurfaceVariant),
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
                        is AsyncImagePainter.State.Loading ->
                            CircularProgressIndicator(color = BeatTreatColors.Purple60, modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
                        is AsyncImagePainter.State.Error ->
                            Icon(Icons.Filled.AccountCircle, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(80.dp))
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            } else {
                Icon(Icons.Filled.AccountCircle, usuario.nombre, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(80.dp))
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(usuario.nombre,   color = Color.White,              fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(usuario.username, color = BeatTreatColors.Purple60, fontSize = 14.sp)

        // Contadores de seguidores/siguiendo
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${usuario.followersCount}",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
                Text("Seguidores", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${usuario.followingCount}",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
                Text("Siguiendo", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }

        if (usuario.bio.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(usuario.bio, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, lineHeight = 18.sp)
        }

        // Botón seguir/dejar de seguir
        if (puedeFollow) {
            Spacer(Modifier.height(16.dp))
            // FIX: botón deshabilitado mientras isFollowLoading para evitar doble tap
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        when {
                            isFollowLoading -> Color.Gray
                            isFollowing     -> BeatTreatColors.SurfaceVariant
                            else            -> BeatTreatColors.Purple60
                        }
                    )
                    .then(
                        if (isFollowLoading) Modifier
                        else Modifier.clickable { onFollowClick() }
                    )
                    .padding(horizontal = 32.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isFollowLoading) {
                        CircularProgressIndicator(
                            color     = Color.White,
                            modifier  = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Procesando...", color = Color.White, fontSize = 14.sp)
                    } else {
                        Icon(
                            imageVector = if (isFollowing) Icons.Filled.PersonRemove else Icons.Filled.PersonAdd,
                            contentDescription = null,
                            tint     = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = if (isFollowing) "Siguiendo" else "Seguir",
                            color      = Color.White,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewOtroUsuarioCard(review: ReviewOtroUsuarioUI, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors   = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(BeatTreatColors.PurpleDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Album, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(review.albumNombre,  color = Color.White,                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(review.albumArtista, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        Icons.Filled.Star, null,
                        tint     = if (i < review.rating) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(review.rating.toString(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(review.fecha, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(review.contenido, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilOtroUsuarioScreenPreview() {
    BeatTreatTheme {
        PerfilOtroUsuarioScreenContent(
            uiState = PerfilOtroUsuarioUIState(
                usuario = OtroUsuarioUI(
                    id = 2, nombre = "María García", username = "@mariagrck",
                    bio = "Reggaeton fan", fotoPerfilUrl = "",
                    followersCount = 142, followingCount = 89
                ),
                isFollowing = false, puedeFollow = true
            ),
            onBackClick = {}
        )
    }
}
