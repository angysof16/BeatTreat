package com.example.login.ui.AlbumDetalle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.login.ui.Resena.ResenaDetalladaUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun AlbumDetalleScreen(
    albumId: Int,
    onBackClick: () -> Unit = {},
    onVerResenasClick: () -> Unit = {},
    onEscribirResenaClick: (Int) -> Unit = {},
    onResenaClick: (resenaId: Int, albumId: Int) -> Unit = { _, _ -> },
    onAutorClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlbumDetalleViewModel
) {
    LaunchedEffect(albumId) { viewModel.cargarAlbum(albumId) }
    val uiState       by viewModel.uiState.collectAsState()
    val currentUserId  = viewModel.getCurrentUserId()

    // Diálogo de confirmación de borrado
    var resenaAEliminar by remember { mutableStateOf<ResenaDetalladaUI?>(null) }
    if (resenaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { resenaAEliminar = null },
            containerColor   = BeatTreatColors.Surface,
            icon  = { Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp)) },
            title = { Text("Eliminar reseña", color = Color.White, fontWeight = FontWeight.Bold) },
            text  = { Text("¿Seguro que quieres eliminar esta reseña?", color = Color.White.copy(alpha = 0.8f)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.eliminarResena(resenaAEliminar!!.firestoreDocId); resenaAEliminar = null },
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { resenaAEliminar = null }) { Text("Cancelar", color = Color.White.copy(alpha = 0.7f)) }
            }
        )
    }

    // Diálogo de edición
    if (uiState.mostrarDialogoEditar && uiState.resenaEditando != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarEditar() },
            containerColor   = BeatTreatColors.Surface,
            title = { Text("Editar reseña", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Calificación
                    Column {
                        Text("Calificación", color = Color.LightGray, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        Row {
                            (1..5).forEach { estrella ->
                                Icon(
                                    imageVector        = if (estrella <= uiState.editRating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Estrella $estrella",
                                    tint               = if (estrella <= uiState.editRating.toInt()) Color(0xFFFFC107) else Color.Gray,
                                    modifier           = Modifier.size(36.dp).clickable { viewModel.onEditRatingChange(estrella.toFloat()) }
                                )
                            }
                        }
                    }
                    // Contenido
                    OutlinedTextField(
                        value         = uiState.editContent,
                        onValueChange = { viewModel.onEditContentChange(it) },
                        label         = { Text("Tu reseña") },
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
                    Text("${uiState.editContent.length} caracteres", color = Color.Gray, fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.End))
                }
            },
            confirmButton = {
                Button(
                    onClick  = { viewModel.guardarEdicion() },
                    enabled  = !uiState.editGuardando && uiState.editContent.isNotBlank() && uiState.editRating > 0f,
                    colors   = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
                ) {
                    if (uiState.editGuardando) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarEditar() }) { Text("Cancelar", color = Color.LightGray) }
            }
        )
    }

    AlbumDetalleScreenContent(
        uiState               = uiState,
        currentUserId         = currentUserId,
        onBackClick           = onBackClick,
        onVerResenasClick     = onVerResenasClick,
        onFavoritoClick       = { viewModel.toggleFavorito() },
        onEscribirResenaClick = { onEscribirResenaClick(albumId) },
        onResenaClick         = { resena -> onResenaClick(resena.id, albumId) },
        onAutorClick          = onAutorClick,
        onEliminarResena      = { resena -> resenaAEliminar = resena },
        onEditarResena        = { resena -> viewModel.abrirEditar(resena) },
        modifier              = modifier
    )
}

// ── Stateless ──
@Composable
fun AlbumDetalleScreenContent(
    uiState: AlbumDetalleUIState,
    currentUserId: String = "",
    onBackClick: () -> Unit,
    onVerResenasClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    onEscribirResenaClick: () -> Unit = {},
    onResenaClick: (ResenaDetalladaUI) -> Unit = {},
    onAutorClick: (String) -> Unit = {},
    onEliminarResena: (ResenaDetalladaUI) -> Unit = {},
    onEditarResena: (ResenaDetalladaUI) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uiState.album == null) {
        AlbumNoEncontrado(onBackClick = onBackClick, modifier = modifier)
        return
    }
    val album = uiState.album

    LazyColumn(
        modifier       = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            AlbumPortadaHeader(
                album = album, esFavorito = uiState.esFavorito,
                onBackClick = onBackClick, onFavoritoClick = onFavoritoClick
            )
        }
        item { AlbumInfoSection(album = album) }
        item { BotonVerResenas(totalResenas = album.totalResenas, calificacion = album.calificacionPromedio, onClick = onVerResenasClick) }

        // Botón escribir reseña
        item {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BeatTreatColors.SurfaceVariant)
                    .clickable { onEscribirResenaClick() }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Edit, null, tint = BeatTreatColors.Purple60, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Escribir reseña", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            Text("Canciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
        }
        items(album.canciones.size) { i ->
            CancionItem(cancion = album.canciones[i], esUltima = i == album.canciones.lastIndex)
        }

        item {
            Spacer(Modifier.height(24.dp))
            Text("Reseñas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
        }

        if (uiState.resenasLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                }
            }
        }
        if (!uiState.resenasLoading && uiState.resenas.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    Text("Aún no hay reseñas para este álbum", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }
        }
        items(uiState.resenas.size) { i ->
            val resena = uiState.resenas[i]
            ResenaItemCompleto(
                resena       = resena,
                esMia        = resena.autorFirestoreUserId == currentUserId,
                onClick      = { onResenaClick(resena) },
                onAutorClick = { if (resena.autorFirestoreUserId.isNotBlank()) onAutorClick(resena.autorFirestoreUserId) },
                onEditar     = { onEditarResena(resena) },
                onEliminar   = { onEliminarResena(resena) }
            )
        }
    }
}

// ── Card de reseña con acciones completas ──
@Composable
fun ResenaItemCompleto(
    resena: ResenaDetalladaUI,
    esMia: Boolean,
    onClick: () -> Unit,
    onAutorClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpandido by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).clickable { onClick() },
        colors   = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Fila autor
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.weight(1f).clickable { onAutorClick() }, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(BeatTreatColors.SurfaceVariant), contentAlignment = Alignment.Center) {
                        if (resena.autorFotoUrl.isNotBlank()) {
                            AsyncImage(resena.autorFotoUrl, resena.autorNombre, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Filled.AccountCircle, resena.autorNombre, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(resena.autorNombre,  color = Color.White,                    fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(resena.autorUsuario, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(resena.fecha, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    if (esMia) {
                        Box {
                            IconButton(onClick = { menuExpandido = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.MoreVert, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                            }
                            DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                                DropdownMenuItem(
                                    text        = { Text("Editar") },
                                    leadingIcon = { Icon(Icons.Filled.Edit, null) },
                                    onClick     = { menuExpandido = false; onEditar() }
                                )
                                DropdownMenuItem(
                                    text        = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                                    leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) },
                                    onClick     = { menuExpandido = false; onEliminar() }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Estrellas
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    val icon = when {
                        i < resena.calificacion.toInt() -> Icons.Filled.Star
                        i == resena.calificacion.toInt() && (resena.calificacion - resena.calificacion.toInt()) >= 0.5f -> Icons.Filled.StarHalf
                        else -> Icons.Filled.StarBorder
                    }
                    Icon(icon, null, tint = if (i < resena.calificacion.toInt()) Color(0xFFFFC107) else Color.Gray, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(6.dp))
                Text(resena.calificacion.toString(), color = Color.White, fontSize = 12.sp)
            }

            Spacer(Modifier.height(8.dp))
            Text(resena.texto, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp,
                lineHeight = 18.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)

            // Footer
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (resena.autorFirestoreUserId.isNotBlank()) {
                    TextButton(onClick = onAutorClick, contentPadding = PaddingValues(0.dp)) {
                        Text("Ver perfil", color = BeatTreatColors.Purple60, fontSize = 12.sp)
                    }
                } else { Spacer(Modifier.width(1.dp)) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ChatBubbleOutline, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ver comentarios", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
        }
    }
}

// ── Fila de canción ──
@Composable
fun CancionItem(cancion: CancionDetalleUI, esUltima: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(BeatTreatColors.SurfaceVariant), contentAlignment = Alignment.Center) {
                Text(cancion.numero.toString(), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.width(14.dp))
            Text(cancion.titulo, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Text(cancion.duracion, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
        if (!esUltima) Divider(color = Color.White.copy(alpha = 0.07f), modifier = Modifier.padding(horizontal = 20.dp))
    }
}

// ── Header portada ──
@Composable
fun AlbumPortadaHeader(album: AlbumDetalleUI, esFavorito: Boolean, onBackClick: () -> Unit, onFavoritoClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(380.dp)) {
        AsyncImage(album.imagenUrl, album.nombre, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BeatTreatColors.Purple60.copy(alpha = 0.3f), Color(0xFF1A1A1A)))))
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(colorStops = arrayOf(0.0f to Color.Black.copy(alpha = 0.4f), 0.55f to Color.Transparent, 1.0f to Color(0xFF121212)))))
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp).align(Alignment.TopStart), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
            IconButton(onClick = onFavoritoClick) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
                    Icon(if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorito",
                        tint = if (esFavorito) Color.Red else Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
        AsyncImage(album.imagenUrl, album.nombre, Modifier.size(180.dp).align(Alignment.Center).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
    }
}

// ── Info section ──
@Composable
fun AlbumInfoSection(album: AlbumDetalleUI, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(album.nombre, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
        Spacer(Modifier.height(4.dp))
        Text(album.artista, color = BeatTreatColors.Purple60, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ChipInfo(album.año); ChipInfo(album.genero); ChipInfo(album.duracionTotal) }
        Spacer(Modifier.height(16.dp))
        Text(album.descripcion, color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(16.dp))
        AlbumCalificacionRow(album.calificacionPromedio, album.totalResenas)
    }
}

@Composable
fun ChipInfo(texto: String, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(20.dp)).background(BeatTreatColors.SurfaceVariant).padding(horizontal = 12.dp, vertical = 5.dp)) {
        Text(texto, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Composable
fun AlbumCalificacionRow(calificacion: Float, totalResenas: Int, modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(calificacion.toString(), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                Text("$totalResenas reseñas", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            EstrellasCalificacion(calificacion)
        }
    }
}

@Composable
fun EstrellasCalificacion(calificacion: Float, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        val entero = calificacion.toInt(); val tieneMedia = (calificacion - entero) >= 0.5f
        repeat(5) { i ->
            val icon = when { i < entero -> Icons.Filled.Star; i == entero && tieneMedia -> Icons.Filled.StarHalf; else -> Icons.Filled.StarBorder }
            Icon(icon, null, tint = if (i < entero || (i == entero && tieneMedia)) Color(0xFFFFC107) else Color.Gray, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun BotonVerResenas(totalResenas: Int, calificacion: Float, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(BeatTreatColors.Purple60, Color(0xFF8B5CF6))))
            .clickable { onClick() }.padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ChatBubbleOutline, null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Ver todas las reseñas", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("$totalResenas opiniones", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
        Box(Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(calificacion.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AlbumNoEncontrado(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.MusicNote, null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Álbum no encontrado", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)) {
                Text("Volver", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumDetalleScreenPreview() {
    BeatTreatTheme {
        AlbumDetalleScreenContent(uiState = AlbumDetalleUIState(), onBackClick = {}, onVerResenasClick = {}, onFavoritoClick = {})
    }
}