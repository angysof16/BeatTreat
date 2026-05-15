// - Toggle LISTA / MAPA en la barra superior
// - MapaReviewsContent: mapa con OSMDroid/OpenStreetMap
// - ReviewInfoWindow: tarjeta inferior al tocar un marcador
package com.example.beattreat.ui.FeedSiguiendo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.beattreat.ui.Resena.ResenaDetalladaUI
import com.example.beattreat.ui.theme.BeatTreatColors
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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
        uiState          = uiState,
        onAutorClick     = onAutorClick,
        onResenaClick    = onResenaClick,
        onLikeClick      = { viewModel.toggleLike(it) },
        onRetry          = { viewModel.cargarFeed() },

        onSwitchVista    = { viewModel.switchVista(it) },
        onMapItemSelect  = { viewModel.onMapItemSelected(it) },
        onMapItemDismiss = { viewModel.onMapItemDismissed() },

        modifier         = modifier
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

    onSwitchVista: (FeedVista) -> Unit,
    onMapItemSelect: (ReviewMapItem) -> Unit,
    onMapItemDismiss: () -> Unit,

    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("feedSiguiendoScreen")
    ) {
        when {
            // ── Cargando ──
            uiState.isLoading -> {
                Column(
                    modifier            = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = BeatTreatColors.Purple60)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Cargando feed en tiempo real...",
                        color    = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                }
            }

            // ── No sigue a nadie ──
            uiState.sinSeguidos -> { SinSeguidosContent() }

            // ── Error sin datos ──
            uiState.errorMessage != null && uiState.reviews.isEmpty() -> {
                ErrorContent(mensaje = uiState.errorMessage, onRetry = onRetry)
            }

            // ── Contenido principal ──
            else -> {

                Column(modifier = Modifier.fillMaxSize()) {
                    VistaToggleBar(
                        vistaActual   = uiState.vistaActual,
                        onSwitchVista = onSwitchVista
                    )
                    when (uiState.vistaActual) {
                        FeedVista.LISTA -> ListaReviewsContent(
                            uiState       = uiState,
                            onAutorClick  = onAutorClick,
                            onResenaClick = onResenaClick,
                            onLikeClick   = onLikeClick
                        )
                        FeedVista.MAPA  -> MapaReviewsContent(
                            mapItems         = uiState.reviewsMapItems,
                            selectedItem     = uiState.selectedMapItem,
                            isLoading        = uiState.isMapLoading,
                            onMarkerClick    = onMapItemSelect,
                            onDismiss        = onMapItemDismiss,
                            onVerResenaClick = { item ->
                                val resena = uiState.reviews.find {
                                    it.firestoreDocId == item.firestoreDocId
                                }
                                if (resena != null) onResenaClick(resena)
                            }
                        )
                    }
                }
            }
        }
    }
}

// ── Toggle Lista / Mapa ───────────────────────────────────────────────────────

@Composable
fun VistaToggleBar(
    vistaActual: FeedVista,
    onSwitchVista: (FeedVista) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            "Siguiendo",
            color      = Color.White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.weight(1f)
        )
        ToggleChip(
            label    = "Lista",
            icon     = Icons.Filled.List,
            selected = vistaActual == FeedVista.LISTA,
            onClick  = { onSwitchVista(FeedVista.LISTA) },
            testTag  = "toggleLista"
        )
        ToggleChip(
            label    = "Mapa",
            icon     = Icons.Filled.Map,
            selected = vistaActual == FeedVista.MAPA,
            onClick  = { onSwitchVista(FeedVista.MAPA) },
            testTag  = "toggleMapa"
        )
    }
}

@Composable
private fun ToggleChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) BeatTreatColors.Purple60 else BeatTreatColors.SurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(16.dp))
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Vista Lista ───────────────────────────────────────────────────────────────

@Composable
fun ListaReviewsContent(
    uiState: FeedSiguiendoUIState,
    onAutorClick: (String) -> Unit,
    onResenaClick: (ResenaDetalladaUI) -> Unit,
    onLikeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier       = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    modifier         = Modifier.fillParentMaxWidth().padding(vertical = 48.dp),
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
            items(
                uiState.reviews,
                key = { it.firestoreDocId.ifBlank { it.id } }
            ) { review ->
                FeedReviewCard(
                    review       = review,
                    isLiked      = review.firestoreDocId in uiState.likedReviewIds,
                    onLikeClick  = { onLikeClick(review.firestoreDocId) },
                    onAutorClick = {
                        if (review.autorFirestoreUserId.isNotBlank())
                            onAutorClick(review.autorFirestoreUserId)
                    },
                    onClick      = { onResenaClick(review) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ── Vista Mapa con OSMDroid ───────────────────────────────────────────────────

/**
 * Mapa OpenStreetMap usando OSMDroid embebido en Compose con AndroidView.
 *
 * Sin API Key. Sin cuenta de Google Cloud.
 * Los tiles se descargan de tile.openstreetmap.org por internet.
 *
 * Al tocar un marcador → muestra [ReviewInfoWindow] en la parte inferior.
 * Al tocar el mapa fuera de un marcador → cierra el InfoWindow.
 */
@Composable
fun MapaReviewsContent(
    mapItems: List<ReviewMapItem>,
    selectedItem: ReviewMapItem?,
    isLoading: Boolean,
    onMarkerClick: (ReviewMapItem) -> Unit,
    onDismiss: () -> Unit,
    onVerResenaClick: (ReviewMapItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Configurar OSMDroid una sola vez (user agent requerido por la política de OSM)
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Box(modifier = modifier.fillMaxSize()) {

        // ── Mapa OSMDroid ────────────────────────────────────────────────────
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .testTag("mapaReviews"),
            factory  = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)   // tiles de OpenStreetMap
                    setMultiTouchControls(true)               // zoom con dos dedos
                    controller.setZoom(5.0)

                    // Centro inicial: Colombia
                    controller.setCenter(GeoPoint(4.570868, -74.297333))

                    // Toque en el mapa fuera de marcadores → cerrar InfoWindow
                    overlays.add(
                        object : org.osmdroid.views.overlay.Overlay() {
                            override fun onSingleTapConfirmed(
                                e: android.view.MotionEvent?,
                                mapView: MapView?
                            ): Boolean {
                                onDismiss()
                                return false  // false = deja que otros overlays también reciban el evento
                            }
                        }
                    )
                }
            },
            update   = { mapView ->
                // Limpiar marcadores anteriores (excepto el overlay de toque global)
                mapView.overlays.removeAll { it is Marker }

                // Añadir un Marker por cada ReviewMapItem
                mapItems.forEach { item ->
                    val geoPoint = GeoPoint(item.latitude, item.longitude)
                    val marker   = Marker(mapView).apply {
                        position          = geoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title             = item.autorNombre
                        snippet           = item.albumNombre
                        // Resaltar el marcador seleccionado con alpha distinto
                        alpha             = if (selectedItem?.firestoreDocId == item.firestoreDocId) 1f else 0.85f

                        setOnMarkerClickListener { _, _ ->
                            onMarkerClick(item)
                            true   // consume el evento → no muestra el InfoWindow nativo de OSMDroid
                        }
                    }
                    mapView.overlays.add(marker)
                }

                // Si hay marcadores, centrar en el primero
                if (mapItems.isNotEmpty() && selectedItem == null) {
                    val first = mapItems.first()
                    mapView.controller.animateTo(
                        GeoPoint(first.latitude, first.longitude),
                        8.0,   // zoom level
                        800L   // duración animación ms
                    )
                }

                mapView.invalidate()   // forzar redibujado
            }
        )

        // ── Spinner de carga ─────────────────────────────────────────────────
        if (isLoading) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        color       = BeatTreatColors.Purple60,
                        modifier    = Modifier.size(14.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Cargando ubicaciones...", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // ── Badge: cantidad de reviews con ubicación ─────────────────────────
        if (!isLoading) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (mapItems.isEmpty()) Color.Black.copy(alpha = 0.65f)
                        else BeatTreatColors.Purple60.copy(alpha = 0.9f)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (mapItems.isEmpty())
                            "Sin reseñas en las últimas 24h"
                        else
                            "${mapItems.size} reseña${if (mapItems.size != 1) "s" else ""} · últimas 24h",
                        color      = Color.White,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── InfoWindow personalizado ─────────────────────────────────────────
        AnimatedVisibility(
            visible  = selectedItem != null,
            enter    = fadeIn() + slideInVertically { it / 2 },
            exit     = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedItem?.let { item ->
                ReviewInfoWindow(
                    item             = item,
                    onDismiss        = onDismiss,
                    onVerResenaClick = { onVerResenaClick(item) }
                )
            }
        }
    }
}

// ── InfoWindow del marcador ───────────────────────────────────────────────────

/**
 * Tarjeta que aparece en la parte inferior al tocar un marcador.
 *
 * Muestra: portada + álbum + autor + estrellas + extracto del review + botón.
 */
@Composable
fun ReviewInfoWindow(
    item: ReviewMapItem,
    onDismiss: () -> Unit,
    onVerResenaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 16.dp)
            .testTag("reviewInfoWindow"),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = BeatTreatColors.Surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Fila superior: álbum + botón cerrar ──────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Portada
                Box(
                    modifier         = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BeatTreatColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.albumImagenUrl.isNotBlank()) {
                        AsyncImage(
                            model              = item.albumImagenUrl,
                            contentDescription = item.albumNombre,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Filled.Album,
                            contentDescription = null,
                            tint     = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Álbum + autor
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.albumNombre,
                        color      = Color.White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        item.albumArtista,
                        color    = BeatTreatColors.Purple60,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(BeatTreatColors.Purple40),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.autorFotoUrl.isNotBlank()) {
                                AsyncImage(
                                    model              = item.autorFotoUrl,
                                    contentDescription = item.autorNombre,
                                    modifier           = Modifier.fillMaxSize(),
                                    contentScale       = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Filled.AccountCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.width(5.dp))
                        Text(item.autorNombre, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(4.dp))
                        Text(item.autorUsuario, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }

                // Botón cerrar
                IconButton(
                    onClick  = onDismiss,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("cerrarInfoWindow")
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        tint     = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Estrellas + likes + fecha ────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        if (i < item.calificacion.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null,
                        tint     = if (i < item.calificacion.toInt()) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(item.calificacion.toString(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint     = Color(0xFFE91E63).copy(alpha = 0.8f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text("${item.likes}", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Spacer(Modifier.width(10.dp))
                Text(item.fecha, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
            }

            Spacer(Modifier.height(8.dp))

            // ── Extracto del review ──────────────────────────────────────────
            Text(
                text       = item.textoResumen,
                color      = Color.White.copy(alpha = 0.85f),
                fontSize   = 13.sp,
                lineHeight = 19.sp,
                maxLines   = 3,
                overflow   = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            // ── Botón ver reseña completa ─────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(BeatTreatColors.Purple60)
                        .clickable { onVerResenaClick() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("verResenaCompleta"),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("Ver reseña completa", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ── Estados vacíos y de error ─────────────────────────────────────────────────

@Composable
private fun SinSeguidosContent(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.PersonSearch,
            contentDescription = null,
            tint     = Color.White.copy(alpha = 0.25f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Aún no sigues a nadie", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Sigue usuarios para ver sus reseñas aquí en tiempo real",
            color    = Color.White.copy(alpha = 0.5f),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ErrorContent(
    mensaje: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.WifiOff,
            contentDescription = null,
            tint     = BeatTreatColors.Error.copy(alpha = 0.6f),
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(mensaje, color = BeatTreatColors.Error, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors  = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
        ) { Text("Reintentar", color = Color.White) }
    }
}

// ── FeedReviewCard ────────────────────────────────────────────────────────────

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
            .testTag("feedReviewCard")
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape  = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier          = Modifier.fillMaxWidth().clickable { onAutorClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier.size(36.dp).clip(CircleShape).background(BeatTreatColors.Purple40),
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        if (i < review.calificacion.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null,
                        tint     = if (i < review.calificacion.toInt()) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(review.calificacion.toString(), color = Color.White, fontSize = 13.sp)
            }

            Spacer(Modifier.height(10.dp))
            Text(review.texto, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, lineHeight = 20.sp, maxLines = 4)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint     = if (isLiked) Color(0xFFE91E63) else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (isLiked) "${review.likes + 1}" else "${review.likes}",
                        color    = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ChatBubbleOutline, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${review.comentarios}", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                }
            }
        }
    }
}