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
    // Navega a EscribirResena pasando el albumId (hash del firestoreId)
    onEscribirResenaClick: (Int) -> Unit = {},
    // Navega a Comentarios de una reseña
    onResenaClick: (resenaId: Int, albumId: Int) -> Unit = { _, _ -> },
    // Navega al perfil del autor (UID de Firestore)
    onAutorClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlbumDetalleViewModel
) {
    LaunchedEffect(albumId) { viewModel.cargarAlbum(albumId) }
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()

    var resenaAEliminar by remember { mutableStateOf<ResenaDetalladaUI?>(null) }
    var resenaAEditar   by remember { mutableStateOf<ResenaDetalladaUI?>(null) }

    // Diálogo de confirmación de borrado
    if (resenaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { resenaAEliminar = null },
            containerColor   = BeatTreatColors.Surface,
            icon             = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp)) },
            title            = { Text("Eliminar reseña", color = Color.White, fontWeight = FontWeight.Bold) },
            text             = { Text("¿Seguro que quieres eliminar esta reseña? No se puede deshacer.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp) },
            confirmButton    = {
                Button(
                    onClick = {
                        resenaAEliminar?.let { r ->
                            // autorFirestoreUserId aquí actúa como firestoreDocId fue mapeado
                            // En FirestoreReviewRepository getReviewsByAlbum, el "id" del par es el docId
                            // y lo guardamos como autorFirestoreUserId = dto.userId; pero el docId real
                            // se perdió. Usamos la función del VM que recibe el docId desde el hashCode.
                            // El firestoreDocId viene del ResenaDetalladaUI — ver nota en comentario abajo.
                            viewModel.eliminarResena(r.firestoreDocId)
                        }
                        resenaAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar", color = Color.White) }
            },
            dismissButton    = {
                TextButton(onClick = { resenaAEliminar = null }) { Text("Cancelar", color = Color.White.copy(alpha = 0.7f)) }
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
                album           = album,
                esFavorito      = uiState.esFavorito,
                onBackClick     = onBackClick,
                onFavoritoClick = onFavoritoClick
            )
        }
        item { AlbumInfoSection(album = album) }
        item {
            BotonVerResenas(
                totalResenas = album.totalResenas,
                calificacion = album.calificacionPromedio,
                onClick      = onVerResenasClick
            )
        }

        // Botón para escribir reseña
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BeatTreatColors.SurfaceVariant)
                        .clickable { onEscribirResenaClick() }
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = BeatTreatColors.Purple60, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Escribir reseña", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Canciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(album.canciones.size) { index ->
            CancionItem(cancion = album.canciones[index], esUltima = index == album.canciones.lastIndex)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Reseñas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (uiState.resenasLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                }
            }
        }
        if (!uiState.resenasLoading && uiState.resenas.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    Text("Aún no hay reseñas para este álbum", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }
        }
        items(uiState.resenas.size) { index ->
            val resena = uiState.resenas[index]
            ResenaItemCompleto(
                resena        = resena,
                esMia         = resena.autorFirestoreUserId == currentUserId,
                onClick       = { onResenaClick(resena) },
                onAutorClick  = { if (resena.autorFirestoreUserId.isNotBlank()) onAutorClick(resena.autorFirestoreUserId) },
                onEliminar    = { onEliminarResena(resena) }
            )
        }
    }
}

// ── Card de reseña COMPLETA dentro del detalle ──
@Composable
fun ResenaItemCompleto(
    resena: ResenaDetalladaUI,
    esMia: Boolean,
    onClick: () -> Unit,
    onAutorClick: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpandido by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila autor
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.weight(1f).clickable { onAutorClick() }
                ) {
                    Box(
                        modifier         = Modifier.size(32.dp).clip(CircleShape).background(BeatTreatColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (resena.autorFotoUrl.isNotBlank()) {
                            AsyncImage(
                                model              = resena.autorFotoUrl,
                                contentDescription = resena.autorNombre,
                                modifier           = Modifier.fillMaxSize(),
                                contentScale       = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Filled.AccountCircle, contentDescription = resena.autorNombre,
                                tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(resena.autorNombre,  color = Color.White,                    fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(resena.autorUsuario, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(resena.fecha, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)

                    // Menú de opciones (solo para reseñas propias)
                    if (esMia) {
                        Box {
                            IconButton(onClick = { menuExpandido = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                            }
                            DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                                DropdownMenuItem(
                                    text        = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                    onClick     = { menuExpandido = false; onEliminar() }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Estrellas
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    val icono = when {
                        index < resena.calificacion.toInt() -> Icons.Filled.Star
                        index == resena.calificacion.toInt() && (resena.calificacion - resena.calificacion.toInt()) >= 0.5f -> Icons.Filled.StarHalf
                        else -> Icons.Filled.StarBorder
                    }
                    Icon(icono, contentDescription = null,
                        tint     = if (index < resena.calificacion.toInt()) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(resena.calificacion.toString(), color = Color.White, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(resena.texto, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp,
                lineHeight = 18.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)

            // Pie: botón "Ver perfil" si tiene UID, y hint de comentarios
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                if (resena.autorFirestoreUserId.isNotBlank()) {
                    TextButton(
                        onClick          = onAutorClick,
                        contentPadding   = PaddingValues(horizontal = 0.dp)
                    ) {
                        Text("Ver perfil", color = BeatTreatColors.Purple60, fontSize = 12.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "Comentarios",
                        tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver comentarios", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
        }
    }
}

// ── Fila de una canción ──
@Composable
fun CancionItem(cancion: CancionDetalleUI, esUltima: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(BeatTreatColors.SurfaceVariant),
                contentAlignment = Alignment.Center) {
                Text(cancion.numero.toString(), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(cancion.titulo, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Text(cancion.duracion, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
        if (!esUltima) Divider(color = Color.White.copy(alpha = 0.07f), modifier = Modifier.padding(horizontal = 20.dp))
    }
}

// ── Header: portada con AsyncImage ──
@Composable
fun AlbumPortadaHeader(
    album: AlbumDetalleUI,
    esFavorito: Boolean,
    onBackClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().height(380.dp)) {
        AsyncImage(
            model              = album.imagenUrl,
            contentDescription = album.nombre,
            modifier           = Modifier.fillMaxSize(),
            contentScale       = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(colors = listOf(BeatTreatColors.Purple60.copy(alpha = 0.3f), Color(0xFF1A1A1A)))
        ))
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(colorStops = arrayOf(
                0.0f  to Color.Black.copy(alpha = 0.4f),
                0.55f to Color.Transparent,
                1.0f  to Color(0xFF121212)
            ))
        ))
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp).align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
            IconButton(onClick = onFavoritoClick) {
                Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center) {
                    Icon(
                        if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (esFavorito) Color.Red else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        AsyncImage(
            model              = album.imagenUrl,
            contentDescription = album.nombre,
            modifier           = Modifier.size(180.dp).align(Alignment.Center).clip(RoundedCornerShape(16.dp)),
            contentScale       = ContentScale.Crop
        )
    }
}

// ── Sección de información ──
@Composable
fun AlbumInfoSection(album: AlbumDetalleUI, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(album.nombre,  color = Color.White,              fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(album.artista, color = BeatTreatColors.Purple60, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipInfo(album.año)
            ChipInfo(album.genero)
            ChipInfo(album.duracionTotal)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(album.descripcion, color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        AlbumCalificacionRow(calificacion = album.calificacionPromedio, totalResenas = album.totalResenas)
    }
}

@Composable
fun ChipInfo(texto: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(20.dp)).background(BeatTreatColors.SurfaceVariant).padding(horizontal = 12.dp, vertical = 5.dp)) {
        Text(texto, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Composable
fun AlbumCalificacionRow(calificacion: Float, totalResenas: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(calificacion.toString(), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                Text("$totalResenas reseñas", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            EstrellasCalificacion(calificacion = calificacion)
        }
    }
}

@Composable
fun EstrellasCalificacion(calificacion: Float, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val entero = calificacion.toInt()
        val tieneMedia = (calificacion - entero) >= 0.5f
        repeat(5) { index ->
            val icono = when {
                index < entero -> Icons.Filled.Star
                index == entero && tieneMedia -> Icons.Filled.StarHalf
                else -> Icons.Filled.StarBorder
            }
            Icon(icono, contentDescription = null,
                tint = if (index < entero || (index == entero && tieneMedia)) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun BotonVerResenas(totalResenas: Int, calificacion: Float, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(colors = listOf(BeatTreatColors.Purple60, Color(0xFF8B5CF6))))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Ver todas las reseñas", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("$totalResenas opiniones de usuarios", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
        Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(calificacion.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AlbumNoEncontrado(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Álbum no encontrado", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
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
        AlbumDetalleScreenContent(
            uiState           = AlbumDetalleUIState(),
            onBackClick       = {},
            onVerResenasClick = {},
            onFavoritoClick   = {}
        )
    }
}