package com.example.beattreat.ui.Resena

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.beattreat.ui.theme.BeatTreatColors
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun UbicacionReviewDialog(
    autorNombre: String,
    albumNombre: String,
    latitude: Double,
    longitude: Double,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(16.dp))
                .background(BeatTreatColors.Surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BeatTreatColors.PurpleDark)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Ubicación de la reseña",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$autorNombre · $albumNombre",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, "Cerrar", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                // Mapa
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(14.0)
                            controller.setCenter(GeoPoint(latitude, longitude))

                            val marker = Marker(this).apply {
                                position = GeoPoint(latitude, longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = autorNombre
                                snippet = albumNombre
                            }
                            overlays.add(marker)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BotonVerUbicacion(
    latitude: Double?,
    longitude: Double?,
    autorNombre: String,
    albumNombre: String,
    modifier: Modifier = Modifier
) {
    if (latitude == null || longitude == null) return

    var mostrarMapa by remember { mutableStateOf(false) }

    if (mostrarMapa) {
        UbicacionReviewDialog(
            autorNombre = autorNombre,
            albumNombre = albumNombre,
            latitude    = latitude,
            longitude   = longitude,
            onDismiss   = { mostrarMapa = false }
        )
    }

    TextButton(
        onClick = { mostrarMapa = true },
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOff,
            contentDescription = "Ver ubicación",
            tint = BeatTreatColors.Purple60,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text("Ver ubicación", color = BeatTreatColors.Purple60, fontSize = 12.sp)
    }
}