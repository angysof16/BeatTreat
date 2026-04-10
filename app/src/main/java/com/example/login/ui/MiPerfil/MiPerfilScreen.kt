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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.login.ui.Perfil.PerfilData
import com.example.login.ui.theme.BeatTreatColors

// ─────────────────────────────────────────────────────────────────────────────
// Stateful entry point
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MiPerfilScreen(
    onAlbumClick: (Int) -> Unit = {},
    onCerrarSesionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MiPerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Snackbar para éxito / error
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.abrirFormularioCrear() },
                containerColor = BeatTreatColors.Purple60
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva reseña", tint = Color.White)
            }
        },
        containerColor = BeatTreatColors.Background
    ) { padding ->
        MiPerfilContent(
            uiState       = uiState,
            onAlbumClick  = onAlbumClick,
            onEditarClick = { viewModel.abrirFormularioEditar(it) },
            onEliminarClick = { viewModel.pedirConfirmarEliminar(it) },
            modifier      = modifier.padding(padding)
        )
    }

    // ── Diálogo: Crear / Editar reseña ───────────────────────────────────────
    if (uiState.mostrarFormulario) {
        FormularioResenaDialog(
            resenaEnEdicion   = uiState.resenaEnEdicion,
            albumId           = uiState.formularioAlbumId,
            rating            = uiState.formularioRating,
            content           = uiState.formularioContent,
            onAlbumIdChange   = viewModel::onAlbumIdChange,
            onRatingChange    = viewModel::onRatingChange,
            onContentChange   = viewModel::onContentChange,
            onGuardar         = viewModel::guardarResena,
            onCancelar        = viewModel::cerrarFormulario,
            isSaving          = uiState.isLoading
        )
    }

    // ── Diálogo: Confirmar eliminar ───────────────────────────────────────────
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
    val perfil = PerfilData.perfilActual   // datos de perfil hardcodeados (id = 1)

    LazyColumn(
        modifier          = modifier.fillMaxSize(),
        contentPadding    = PaddingValues(bottom = 80.dp)
    ) {

        // ── Header: avatar + nombre ──────────────────────────────────────────
        item {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .background(BeatTreatColors.Surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier          = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(BeatTreatColors.SurfaceVariant),
                    contentAlignment  = Alignment.Center
                ) {
                    if (perfil.fotoPerfilUrl.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model              = perfil.fotoPerfilUrl,
                            contentDescription = perfil.nombre,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector        = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(60.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(perfil.nombre,   color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(perfil.usuario,  color = Color.LightGray, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    StatChip("Siguiendo", perfil.siguiendo)
                    StatChip("Seguidores", perfil.seguidores)
                    StatChip("Reseñas", uiState.misResenas.size)
                }
            }
        }

        // ── Separador / título sección ───────────────────────────────────────
        item {
            Spacer(Modifier.height(12.dp))
            Text(
                text     = "Mis reseñas",
                color    = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(4.dp))
        }

        // ── Loading ──────────────────────────────────────────────────────────
        if (uiState.isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                }
            }
        }

        // ── Empty state ──────────────────────────────────────────────────────
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

        // ── Lista de reseñas ─────────────────────────────────────────────────
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
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors    = CardDefaults.cardColors(containerColor = BeatTreatColors.Surface),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Fila superior: portada + info álbum + menú opciones
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Portada álbum
                Box(
                    modifier         = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(BeatTreatColors.SurfaceVariant)
                        .clickable { onAlbumClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (resena.albumCover.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model              = resena.albumCover,
                            contentDescription = resena.albumTitulo,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Filled.Album, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                    }
                }

                Spacer(Modifier.width(10.dp))

                // Nombre álbum + artista
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        resena.albumTitulo,
                        color      = Color.White,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(resena.albumArtist, color = Color.LightGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    EstrellaRating(rating = resena.rating)
                }

                // Menú contextual (editar / eliminar)
                Box {
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded         = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        DropdownMenuItem(
                            text         = { Text("Editar") },
                            leadingIcon  = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick      = { menuExpandido = false; onEditarClick() }
                        )
                        DropdownMenuItem(
                            text         = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                            leadingIcon  = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick      = { menuExpandido = false; onEliminarClick() }
                        )
                    }
                }
            }

            // Contenido de la reseña
            Spacer(Modifier.height(10.dp))
            Text(resena.content, color = Color(0xFFDDDDDD), fontSize = 13.sp, lineHeight = 18.sp)

            // Fecha
            if (resena.createdAt.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    formatearFecha(resena.createdAt),
                    color    = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Diálogo Crear / Editar reseña
// ─────────────────────────────────────────────────────────────────────────────
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
        title = {
            Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // ── ID de álbum (solo en modo crear) ──────────────────────
                if (!esEdicion) {
                    OutlinedTextField(
                        value         = if (albumId == 0) "" else albumId.toString(),
                        onValueChange = { txt -> txt.toIntOrNull()?.let { onAlbumIdChange(it) } },
                        label         = { Text("ID del álbum") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedTextColor   = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedLabelColor  = BeatTreatColors.Purple60,
                            unfocusedLabelColor = Color.Gray,
                            focusedBorderColor  = BeatTreatColors.Purple60,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                } else {
                    // En edición mostramos el álbum como texto no editable
                    Text(
                        "Álbum: ${resenaEnEdicion!!.albumTitulo}",
                        color    = Color.LightGray,
                        fontSize = 13.sp
                    )
                }

                // ── Rating con estrellas ──────────────────────────────────
                Column {
                    Text("Calificación", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Row {
                        (1..5).forEach { estrella ->
                            Icon(
                                imageVector        = if (estrella <= rating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "Estrella $estrella",
                                tint               = if (estrella <= rating.toInt()) Color(0xFFFFC107) else Color.Gray,
                                modifier           = Modifier
                                    .size(32.dp)
                                    .clickable { onRatingChange(estrella.toFloat()) }
                            )
                        }
                    }
                }

                // ── Texto de la reseña ────────────────────────────────────
                OutlinedTextField(
                    value         = content,
                    onValueChange = onContentChange,
                    label         = { Text("Escribe tu reseña...") },
                    minLines      = 4,
                    maxLines      = 8,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor    = Color.White,
                        unfocusedTextColor  = Color.LightGray,
                        focusedLabelColor   = BeatTreatColors.Purple60,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor  = BeatTreatColors.Purple60,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                // Contador de caracteres
                Text(
                    "${content.length} caracteres",
                    color    = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = onGuardar,
                enabled  = !isSaving && content.isNotBlank() && rating > 0f,
                colors   = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
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
// ─────────────────────────────────────────────────────────────────────────────
// Diálogo Confirmar eliminar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ConfirmarEliminarDialog(
    resena: MiResenaUI,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor   = BeatTreatColors.Surface,
        icon = {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
        },
        title = {
            Text("Eliminar reseña", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "¿Seguro que quieres eliminar tu reseña de \"${resena.albumTitulo}\"? Esta acción no se puede deshacer.",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancelar", color = Color.LightGray)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────
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
    // Intenta cortar el ISO string: "2024-05-12T..." → "12 may 2024"
    return try {
        val partes = raw.substringBefore("T").split("-")
        "${partes[2]} / ${partes[1]} / ${partes[0]}"
    } catch (e: Exception) {
        raw
    }
}

