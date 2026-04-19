package com.example.login.ui.MiPerfil

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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.login.R
import com.example.login.ui.Perfil.PerfilData
import com.example.login.ui.theme.BeatTreatColors

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MiPerfilScreen(
    onAlbumClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {},
    onEscribirResenaClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MiPerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        val msg = uiState.successMessage ?: uiState.errorMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // ── TopBar estilo BeatTreat (consistente con el resto de la app) ──
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cuadro del logo (esquina superior izquierda)
                Box(
                    modifier         = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(bottomEnd = 12.dp))
                        .background(Color(0xFF1A1A1A)),
                    contentAlignment = Alignment.Center
                ) {
                    coil.compose.AsyncImage(
                        model              = "https://cdn.phototourl.com/free/2026-04-16-f75c12f6-7aa0-4e5d-959f-803340165dd0.png",
                        contentDescription = "Logo BeatTreat",
                        modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale       = ContentScale.Fit
                    )
                }
                // Barra morada con botón atrás + título
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(bottomStart = 12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                        text       = "BeatTreat",
                        color      = Color.White,
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = JaroFont,
                        modifier   = Modifier.weight(1f).padding(start = 4.dp)
                    )
                    Text(
                        text     = "Mis reseñas",
                        color    = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onEscribirResenaClick,
                containerColor = BeatTreatColors.Purple60
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva reseña", tint = Color.White)
            }
        },
        containerColor = BeatTreatColors.Background
    ) { padding ->
        MiPerfilContent(
            uiState         = uiState,
            onAlbumClick    = onAlbumClick,
            onEditarClick   = { viewModel.abrirFormularioEditar(it) },
            onEliminarClick = { viewModel.pedirConfirmarEliminar(it) },
            modifier        = modifier.padding(padding)
        )
    }

    // Edit dialog
    if (uiState.mostrarFormulario) {
        FormularioResenaDialog(
            resenaEnEdicion = uiState.resenaEnEdicion,
            albumId         = uiState.formularioAlbumId,
            rating          = uiState.formularioRating,
            content         = uiState.formularioContent,
            onAlbumIdChange = {},
            onRatingChange  = viewModel::onRatingChange,
            onContentChange = viewModel::onContentChange,
            onGuardar       = viewModel::guardarResena,
            onCancelar      = viewModel::cerrarFormulario,
            isSaving        = uiState.isLoading
        )
    }

    if (uiState.mostrarConfirmarEliminar && uiState.resenaAEliminar != null) {
        ConfirmarEliminarDialog(
            resena    = uiState.resenaAEliminar!!,
            onConfirm = viewModel::confirmarEliminar,
            onCancel  = viewModel::cancelarEliminar
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stateless content
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MiPerfilContent(
    uiState: MiPerfilUIState,
    onAlbumClick: (Int) -> Unit,
    onEditarClick: (MiResenaUI) -> Unit,
    onEliminarClick: (MiResenaUI) -> Unit,
    modifier: Modifier = Modifier
) {
    val perfil = PerfilData.perfilActual

    LazyColumn(
        modifier       = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .background(BeatTreatColors.Surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier         = Modifier.size(88.dp).clip(CircleShape).background(BeatTreatColors.SurfaceVariant),
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
                                is AsyncImagePainter.State.Loading ->
                                    CircularProgressIndicator(color = BeatTreatColors.Purple60, modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
                                is AsyncImagePainter.State.Error ->
                                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                                else -> SubcomposeAsyncImageContent()
                            }
                        }
                    } else {
                        Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(perfil.nombre,  color = Color.White,     fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(perfil.usuario, color = Color.LightGray, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    StatChip("Siguiendo", perfil.siguiendo)
                    StatChip("Seguidores", perfil.seguidores)
                    StatChip("Reseñas", uiState.misResenas.size)
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            Text(
                text       = "Mis reseñas",
                color      = Color.White,
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(4.dp))
        }

        if (uiState.isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                }
            }
        }

        if (!uiState.isLoading && uiState.misResenas.isEmpty()) {
            item {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.RateReview, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Aún no has escrito reseñas", color = Color.Gray, fontSize = 14.sp)
                    Text("Toca el + para crear la primera", color = Color.DarkGray, fontSize = 12.sp)
                }
            }
        }

        items(uiState.misResenas, key = { it.id }) { resena ->
            MiResenaCard(
                resena          = resena,
                onAlbumClick    = { onAlbumClick(resena.albumId) },
                onEditarClick   = { onEditarClick(resena) },
                onEliminarClick = { onEliminarClick(resena) }
            )
            Divider(color = BeatTreatColors.SurfaceVariant, thickness = 0.5.dp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Card individual de reseña
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MiResenaCard(
    resena: MiResenaUI,
    onAlbumClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        colors    = CardDefaults.cardColors(containerColor = BeatTreatColors.Surface),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BeatTreatColors.SurfaceVariant)
                        .clickable { onAlbumClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (resena.albumCover.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model              = resena.albumCover,
                            contentDescription = resena.albumTitulo,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        ) {
                            when (painter.state) {
                                is AsyncImagePainter.State.Loading ->
                                    CircularProgressIndicator(color = BeatTreatColors.Purple60, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                is AsyncImagePainter.State.Error ->
                                    Icon(Icons.Filled.Album, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                                else -> SubcomposeAsyncImageContent()
                            }
                        }
                    } else {
                        Icon(Icons.Filled.Album, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        resena.albumTitulo,
                        color      = Color.White,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        resena.albumArtist,
                        color    = BeatTreatColors.Purple60,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    EstrellaRating(rating = resena.rating)
                }

                Box {
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                        DropdownMenuItem(
                            text        = { Text("Editar") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick     = { menuExpandido = false; onEditarClick() }
                        )
                        DropdownMenuItem(
                            text        = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick     = { menuExpandido = false; onEliminarClick() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(resena.content, color = Color(0xFFDDDDDD), fontSize = 13.sp, lineHeight = 18.sp)

            if (resena.createdAt.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(formatearFecha(resena.createdAt), color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun FormularioResenaDialog(
    resenaEnEdicion: MiResenaUI?,
    albumId: Int,
    rating: Float,
    content: String,
    onAlbumIdChange: (Int) -> Unit,
    onRatingChange: (Float) -> Unit,
    onContentChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    isSaving: Boolean
) {
    val esEdicion = resenaEnEdicion != null
    val titulo    = if (esEdicion) "Editar reseña" else "Nueva reseña"

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor   = BeatTreatColors.Surface,
        title = { Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (esEdicion) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(BeatTreatColors.SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (resenaEnEdicion!!.albumCover.isNotBlank()) {
                                SubcomposeAsyncImage(
                                    model              = resenaEnEdicion.albumCover,
                                    contentDescription = resenaEnEdicion.albumTitulo,
                                    modifier           = Modifier.fillMaxSize(),
                                    contentScale       = ContentScale.Crop
                                ) {
                                    when (painter.state) {
                                        is AsyncImagePainter.State.Error ->
                                            Icon(Icons.Filled.Album, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                                        else -> SubcomposeAsyncImageContent()
                                    }
                                }
                            } else {
                                Icon(Icons.Filled.Album, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(resenaEnEdicion!!.albumTitulo, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(resenaEnEdicion.albumArtist,   color = BeatTreatColors.Purple60, fontSize = 12.sp)
                        }
                    }
                }

                Column {
                    Text("Calificación", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Row {
                        (1..5).forEach { estrella ->
                            Icon(
                                imageVector        = if (estrella <= rating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "Estrella $estrella",
                                tint               = if (estrella <= rating.toInt()) Color(0xFFFFC107) else Color.Gray,
                                modifier           = Modifier.size(32.dp).clickable { onRatingChange(estrella.toFloat()) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value         = content,
                    onValueChange = onContentChange,
                    label         = { Text("Escribe tu reseña...") },
                    minLines      = 4,
                    maxLines      = 8,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.LightGray,
                        focusedLabelColor    = BeatTreatColors.Purple60,
                        unfocusedLabelColor  = Color.Gray,
                        focusedBorderColor   = BeatTreatColors.Purple60,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Text("${content.length} caracteres", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
            }
        },
        confirmButton = {
            Button(
                onClick = onGuardar,
                enabled = !isSaving && content.isNotBlank() && rating > 0f,
                colors  = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(if (esEdicion) "Guardar cambios" else "Publicar", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = Color.LightGray)
            }
        }
    )
}

@Composable
fun ConfirmarEliminarDialog(
    resena: MiResenaUI,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor   = BeatTreatColors.Surface,
        icon  = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp)) },
        title = { Text("Eliminar reseña", color = Color.White, fontWeight = FontWeight.Bold) },
        text  = {
            Text(
                "¿Seguro que quieres eliminar tu reseña de \"${resena.albumTitulo}\"? Esta acción no se puede deshacer.",
                color = Color.LightGray, fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancelar", color = Color.LightGray) }
        }
    )
}

@Composable
private fun StatChip(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
private fun EstrellaRating(rating: Float) {
    Row {
        (1..5).forEach { i ->
            Icon(
                imageVector        = if (i <= rating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint               = if (i <= rating.toInt()) Color(0xFFFFC107) else Color.Gray,
                modifier           = Modifier.size(14.dp)
            )
        }
    }
}

private fun formatearFecha(raw: String): String {
    return try {
        val millis = raw.toLongOrNull()
        if (millis != null) {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            sdf.format(java.util.Date(millis))
        } else {
            val partes = raw.substringBefore("T").split("-")
            "${partes[2]} / ${partes[1]} / ${partes[0]}"
        }
    } catch (e: Exception) {
        raw
    }
}