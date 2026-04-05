package com.example.login.ui.Perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.login.R
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Stateful ──
@Composable
fun ProfileScreen(
    onSearchClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSiguiendoClick: () -> Unit = {},
    onSeguidoresClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onAlbumClick: (Int) -> Unit = {},
    onVerTodasResenasClick: () -> Unit = {},
    onResenaClick: (ResenaUI) -> Unit = {},
    onCerrarSesionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.cerrarSesionExitoso) {
        if (uiState.cerrarSesionExitoso) {
            viewModel.resetCerrarSesion()
            onCerrarSesionClick()
        }
    }

    val perfil = uiState.perfil ?: return

    ProfileScreenContent(
        uiState                = uiState,
        perfil                 = perfil,
        onSearchClick          = onSearchClick,
        onEditProfileClick     = onEditProfileClick,
        onSiguiendoClick       = onSiguiendoClick,
        onSeguidoresClick      = onSeguidoresClick,
        onMessageClick         = onMessageClick,
        onAlbumClick           = onAlbumClick,
        onVerTodasResenasClick = onVerTodasResenasClick,
        onResenaClick          = onResenaClick,
        onCerrarSesionClick    = { viewModel.cerrarSesion() },
        modifier               = modifier
    )
}

// ── Stateless ──
@Composable
fun ProfileScreenContent(
    uiState: ProfileUIState,
    perfil: PerfilUI,
    onSearchClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSiguiendoClick: () -> Unit,
    onSeguidoresClick: () -> Unit,
    onMessageClick: () -> Unit,
    onAlbumClick: (Int) -> Unit,
    onVerTodasResenasClick: () -> Unit,
    onResenaClick: (ResenaUI) -> Unit,
    onCerrarSesionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            containerColor   = BeatTreatColors.SurfaceVariant,
            title = {
                Text(
                    text       = "Cerrar sesión",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )
            },
            text = {
                Text(
                    text     = "¿Estás seguro de que deseas cerrar sesión?",
                    color    = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogo = false; onCerrarSesionClick() },
                    colors  = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Error)
                ) {
                    Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarProfile(
            onSearchClick       = onSearchClick,
            onCerrarSesionClick = { mostrarDialogo = true }
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ProfileHeader(
                    perfil             = perfil,
                    isUploadingPhoto   = uiState.isUploadingPhoto,
                    onEditProfileClick = onEditProfileClick,
                    onSiguiendoClick   = onSiguiendoClick,
                    onSeguidoresClick  = onSeguidoresClick,
                    onMessageClick     = onMessageClick
                )
            }
            uiState.errorMessage?.let { msg ->
                item {
                    Text(
                        text     = msg,
                        color    = BeatTreatColors.Error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
            item {
                AlbumSection(albumes = uiState.albumesFavoritos, onAlbumClick = onAlbumClick)
                Spacer(modifier = Modifier.height(22.dp))
            }
            item {
                ReviewsSection(
                    resenas         = uiState.resenas,
                    fotoPerfilUrl   = uiState.fotoPerfilUrl,
                    onVerTodasClick = onVerTodasResenasClick,
                    onResenaClick   = onResenaClick
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── TopBar: siempre ícono de cerrar sesión ──
@Composable
fun TopBarProfile(
    onSearchClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
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
                contentDescription = "Logo",
                modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                contentScale       = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(bottomStart = 12.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "BeatTreat",
                color      = Color.White,
                fontSize   = 28.sp,
                fontFamily = JaroFont,
                modifier   = Modifier.weight(1f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint               = Color.White,
                        modifier           = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = onCerrarSesionClick) {
                    Icon(
                        Icons.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint               = Color.White,
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// ── Header del perfil ──
@Composable
fun ProfileHeader(
    perfil: PerfilUI,
    isUploadingPhoto: Boolean,
    onEditProfileClick: () -> Unit,
    onSiguiendoClick: () -> Unit,
    onSeguidoresClick: () -> Unit,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1230))
        ) {
            PerfilBanner(fotoBannerUrl = perfil.fotoBannerUrl)
            Row(modifier = Modifier.fillMaxWidth()) {
                PerfilAvatar(
                    perfil           = perfil,
                    isUploadingPhoto = isUploadingPhoto,
                    onEditClick      = onEditProfileClick
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.padding(top = 18.dp, end = 12.dp)) {
                    Row(modifier = Modifier.height(30.dp)) {
                        ButtonSmall(text = "Siguiendo", onClick = onSiguiendoClick)
                        Spacer(Modifier.width(6.dp))
                        ButtonSmall(text = "Message",   onClick = onMessageClick)
                    }
                }
            }
            PerfilInfo(
                perfil            = perfil,
                onSiguiendoClick  = onSiguiendoClick,
                onSeguidoresClick = onSeguidoresClick
            )
        }
    }
}

// ── Banner ──
@Composable
fun PerfilBanner(fotoBannerUrl: String, modifier: Modifier = Modifier) {
    if (fotoBannerUrl.isNotBlank()) {
        SubcomposeAsyncImage(
            model              = fotoBannerUrl,
            contentDescription = "Banner",
            modifier           = modifier.fillMaxWidth().height(130.dp),
            contentScale       = ContentScale.Crop
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Error -> {
                    Box(
                        modifier         = modifier.fillMaxWidth().height(130.dp)
                            .background(BeatTreatColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = null,
                            tint               = Color.White.copy(alpha = 0.2f),
                            modifier           = Modifier.size(40.dp)
                        )
                    }
                }
                else -> SubcomposeAsyncImageContent()
            }
        }
    } else {
        Box(
            modifier         = modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(BeatTreatColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Image,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.2f),
                modifier           = Modifier.size(40.dp)
            )
        }
    }
}

// ── Avatar ──
// Sin foto_perfil hardcodeada: si no hay URL muestra AccountCircle
@Composable
fun PerfilAvatar(
    perfil: PerfilUI,
    isUploadingPhoto: Boolean,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(start = 16.dp)) {
        Box(
            modifier         = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(BeatTreatColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (perfil.fotoPerfilUrl.isNotBlank()) {
                SubcomposeAsyncImage(
                    model              = perfil.fotoPerfilUrl,
                    contentDescription = perfil.nombre,
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
                            // Error de red: ícono genérico, sin drawable hardcodeado
                            Icon(
                                imageVector        = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                tint               = Color.White.copy(alpha = 0.6f),
                                modifier           = Modifier.size(64.dp)
                            )
                        }
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            } else {
                // Sin URL aún: ícono genérico, sin drawable hardcodeado
                Icon(
                    imageVector        = Icons.Filled.AccountCircle,
                    contentDescription = perfil.nombre,
                    tint               = Color.White.copy(alpha = 0.6f),
                    modifier           = Modifier.size(64.dp)
                )
            }

            // Overlay de carga mientras se sube nueva foto
            if (isUploadingPhoto) {
                Box(
                    modifier         = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color       = BeatTreatColors.Purple60,
                        modifier    = Modifier.size(28.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        // Botón editar
        Box(
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.BottomEnd)
                .background(Color.Black, CircleShape)
                .clickable { onEditClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "Editar perfil",
                tint               = Color.White,
                modifier           = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun PerfilInfo(
    perfil: PerfilUI,
    onSiguiendoClick: () -> Unit,
    onSeguidoresClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(perfil.nombre,  color = Color.White,     fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(perfil.usuario, color = Color.LightGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text(
                text     = "${perfil.siguiendo} Siguiendo",
                color    = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onSiguiendoClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text     = "${perfil.seguidores} Seguidores",
                color    = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onSeguidoresClick() }
            )
        }
    }
}

@Composable
fun ButtonSmall(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B1FA6))
    ) {
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

// ── Álbumes ──
@Composable
fun AlbumSection(
    albumes: List<AlbumPerfilUI>,
    onAlbumClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text       = "Álbumes Favoritos",
            color      = Color.White,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(albumes) { album ->
                AlbumPerfilItem(album = album, onClick = { onAlbumClick(album.id) })
            }
        }
    }
}

@Composable
fun AlbumPerfilItem(album: AlbumPerfilUI, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clickable { onClick() }) {
        if (album.imagenRes != 0) {
            Image(
                painter            = painterResource(id = album.imagenRes),
                contentDescription = album.nombre,
                modifier           = Modifier.size(105.dp).clip(RoundedCornerShape(8.dp)),
                contentScale       = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BeatTreatColors.SurfaceVariant)
            )
        }
        Text(
            text     = album.nombre,
            color    = Color.White,
            fontSize = 11.sp,
            modifier = Modifier
                .width(105.dp)
                .background(Color(0xFF24124A))
                .padding(vertical = 5.dp, horizontal = 6.dp)
        )
    }
}

// ── Reseñas ──
@Composable
fun ReviewsSection(
    resenas: List<ResenaUI>,
    fotoPerfilUrl: String,
    onVerTodasClick: () -> Unit,
    onResenaClick: (ResenaUI) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "Reseñas recientes",
                color      = Color.White,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.clickable { onVerTodasClick() }
            ) {
                Text("Ver todas", color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Ver todas",
                    tint               = Color.White,
                    modifier           = Modifier.size(14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        resenas.forEach { resena ->
            ReviewCard(
                resena        = resena,
                fotoPerfilUrl = fotoPerfilUrl,
                onClick       = { onResenaClick(resena) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── ReviewCard sin foto_perfil hardcodeada ──
@Composable
fun ReviewCard(
    resena: ResenaUI,
    fotoPerfilUrl: String = "",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        border   = BorderStroke(1.dp, Color.Gray),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF2A0A57))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar sin drawable hardcodeado
                    Box(
                        modifier         = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BeatTreatColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoPerfilUrl.isNotBlank()) {
                            SubcomposeAsyncImage(
                                model              = fotoPerfilUrl,
                                contentDescription = resena.autorNombre,
                                modifier           = Modifier.fillMaxSize(),
                                contentScale       = ContentScale.Crop
                            ) {
                                when (painter.state) {
                                    is AsyncImagePainter.State.Error -> {
                                        Icon(
                                            imageVector        = Icons.Filled.AccountCircle,
                                            contentDescription = null,
                                            tint               = Color.White,
                                            modifier           = Modifier.size(42.dp)
                                        )
                                    }
                                    else -> SubcomposeAsyncImageContent()
                                }
                            }
                        } else {
                            Icon(
                                imageVector        = Icons.Filled.AccountCircle,
                                contentDescription = resena.autorNombre,
                                tint               = Color.White,
                                modifier           = Modifier.size(42.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(resena.autorNombre,  color = Color.White,     fontSize = 14.sp)
                        Text(resena.autorUsuario, color = Color.LightGray, fontSize = 11.sp)
                    }
                }
                ReviewStats(comentarios = resena.comentarios, likes = resena.likes)
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "Opciones",
                        tint               = Color.White,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(resena.texto, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun ReviewStats(comentarios: Int, likes: Int, modifier: Modifier = Modifier) {
    Row(
        modifier          = modifier
            .background(Color.Black, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.ChatBubbleOutline,
            contentDescription = "Comentarios",
            tint               = Color.White,
            modifier           = Modifier.size(13.dp)
        )
        Text(" $comentarios", color = Color.White, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            Icons.Filled.FavoriteBorder,
            contentDescription = "Likes",
            tint               = Color.White,
            modifier           = Modifier.size(13.dp)
        )
        Text(" $likes", color = Color.White, fontSize = 11.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    BeatTreatTheme {
        PerfilData.perfilActual.let { perfil ->
            ProfileScreenContent(
                uiState                = ProfileUIState(perfil = perfil),
                perfil                 = perfil,
                onSearchClick          = {},
                onEditProfileClick     = {},
                onSiguiendoClick       = {},
                onSeguidoresClick      = {},
                onMessageClick         = {},
                onAlbumClick           = {},
                onVerTodasResenasClick = {},
                onResenaClick          = {},
                onCerrarSesionClick    = {}
            )
        }
    }
}