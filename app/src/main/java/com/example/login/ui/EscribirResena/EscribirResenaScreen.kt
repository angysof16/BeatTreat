package com.example.login.ui.EscribirResena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.data.dto.AlbumDto
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

private fun etiquetaCalificacion(valor: Int) = when (valor) {
    1    -> "Muy malo"
    2    -> "Malo"
    3    -> "Regular"
    4    -> "Bueno"
    5    -> "Excelente"
    else -> ""
}

// ── Stateful ──
@Composable
fun EscribirResenaScreen(
    onBackClick: () -> Unit = {},
    onPublicarClick: (String, Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    viewModel: EscribirResenaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.publicadoExitoso) {
        if (uiState.publicadoExitoso) {
            onPublicarClick(uiState.textoResena, uiState.calificacion)
            viewModel.resetPublicado()
        }
    }

    EscribirResenaScreenContent(
        uiState              = uiState,
        onTextoChange        = { viewModel.onTextoChange(it) },
        onCalificacionChange = { viewModel.onCalificacionChange(it) },
        onAlbumSeleccionado  = { viewModel.onAlbumSeleccionado(it) },
        onBackClick          = onBackClick,
        onPublicarClick      = { viewModel.publicarResena() },
        modifier             = modifier
    )
}

// ── Stateless ──
@Composable
fun EscribirResenaScreenContent(
    uiState: EscribirResenaUIState,
    onTextoChange: (String) -> Unit,
    onCalificacionChange: (Float) -> Unit,
    onAlbumSeleccionado: (String) -> Unit,
    onBackClick: () -> Unit,
    onPublicarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val puedePublicar = uiState.textoResena.isNotBlank()
            && uiState.calificacion > 0
            && uiState.albumId != 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarEscribirResena(
            onBackClick       = onBackClick,
            onPublicarClick   = onPublicarClick,
            habilitarPublicar = puedePublicar
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Nueva reseña",
                color      = Color.White,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text     = "Comparte tu opinión con la comunidad",
                color    = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )

            // Mensaje de error
            uiState.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text     = msg,
                    color    = BeatTreatColors.Error,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Álbum",
                color      = Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.padding(bottom = 8.dp)
            )

            // Selector con álbumes del backend
            SelectorAlbumBackend(
                albumsBackend       = uiState.albumesBackend,
                albumesCargando     = uiState.albumesCargando,
                albumSeleccionado   = uiState.albumSeleccionado,
                onAlbumSeleccionado = onAlbumSeleccionado
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Calificación",
                color      = Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.padding(bottom = 12.dp)
            )
            CalificacionSelector(
                calificacion         = uiState.calificacion,
                onCalificacionChange = onCalificacionChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Tu opinión",
                color      = Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.padding(bottom = 8.dp)
            )
            CampoOpinion(
                texto         = uiState.textoResena,
                onTextoChange = onTextoChange
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick  = onPublicarClick,
                enabled  = puedePublicar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = BeatTreatColors.Purple60,
                    contentColor           = Color.White,
                    disabledContainerColor = BeatTreatColors.SurfaceVariant,
                    disabledContentColor   = Color.White.copy(alpha = 0.4f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Publicar reseña",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ── Selector de álbum usando lista del backend ──
@Composable
fun SelectorAlbumBackend(
    albumsBackend: List<AlbumDto>,
    albumesCargando: Boolean,
    albumSeleccionado: String,
    onAlbumSeleccionado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    var busqueda  by remember { mutableStateOf("") }

    // Construye las etiquetas "título — artista" con los IDs reales del backend
    val etiquetas = remember(albumsBackend) {
        albumsBackend.map { "${it.title} — ${it.artist}" }
    }

    val etiquetasFiltradas = remember(busqueda, etiquetas) {
        if (busqueda.isBlank()) etiquetas
        else etiquetas.filter { it.lowercase().contains(busqueda.lowercase()) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (!albumesCargando) expandido = !expandido },
            colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
            shape  = if (expandido)
                RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
            else
                RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(BeatTreatColors.Purple60, BeatTreatColors.PurpleDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (albumesCargando) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(24.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Filled.Album,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (albumSeleccionado.isBlank()) {
                        Text(
                            text  = if (albumesCargando) "Cargando álbumes..." else "Seleccionar álbum",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 15.sp
                        )
                        if (!albumesCargando) {
                            Text(
                                text     = "Toca para buscar",
                                color    = Color.White.copy(alpha = 0.3f),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Text(
                            text       = albumSeleccionado,
                            color      = Color.White,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (!albumesCargando) {
                    Icon(
                        imageVector        = if (expandido) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint               = Color.White.copy(alpha = 0.5f),
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }
        }

        if (expandido && !albumesCargando) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
                shape    = RoundedCornerShape(
                    topStart    = 0.dp,
                    topEnd      = 0.dp,
                    bottomStart = 14.dp,
                    bottomEnd   = 14.dp
                )
            ) {
                Column {
                    // Campo de búsqueda
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint               = Color.White.copy(alpha = 0.5f),
                            modifier           = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value         = busqueda,
                            onValueChange = { busqueda = it },
                            placeholder   = {
                                Text(
                                    "Buscar...",
                                    color    = Color.White.copy(alpha = 0.35f),
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors   = TextFieldDefaults.colors(
                                focusedContainerColor   = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor   = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor        = Color.White,
                                unfocusedTextColor      = Color.White,
                                cursorColor             = BeatTreatColors.Purple60
                            ),
                            singleLine = true
                        )
                    }
                    Divider(color = Color.White.copy(alpha = 0.08f))

                    if (etiquetasFiltradas.isEmpty()) {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = "No se encontraron álbumes",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                            items(etiquetasFiltradas) { etiqueta ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onAlbumSeleccionado(etiqueta)
                                            expandido = false
                                            busqueda  = ""
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BeatTreatColors.Purple60)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text     = etiqueta,
                                        color    = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                                Divider(color = Color.White.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── TopBar ──
@Composable
fun TopBarEscribirResena(
    onBackClick: () -> Unit,
    onPublicarClick: () -> Unit,
    habilitarPublicar: Boolean,
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
                contentDescription = "Logo BeatTreat",
                modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                contentScale       = ContentScale.Fit
            )
        }
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
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (habilitarPublicar) Color.White else Color.White.copy(alpha = 0.2f)
                    )
                    .clickable(enabled = habilitarPublicar) { onPublicarClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text       = "Publicar",
                    color      = if (habilitarPublicar) BeatTreatColors.Purple60
                    else Color.White.copy(alpha = 0.4f),
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Selector de calificación ──
@Composable
fun CalificacionSelector(
    calificacion: Float,
    onCalificacionChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
        shape    = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    val llena = index < calificacion.toInt()
                    IconButton(
                        onClick  = { onCalificacionChange((index + 1).toFloat()) },
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(
                            imageVector        = if (llena) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Estrella ${index + 1}",
                            tint               = if (llena) Color(0xFFFFC107)
                            else Color.White.copy(alpha = 0.25f),
                            modifier           = Modifier.size(40.dp)
                        )
                    }
                }
            }

            if (calificacion > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(BeatTreatColors.Purple60, Color(0xFF8B5CF6))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text       = etiquetaCalificacion(calificacion.toInt()),
                        color      = Color.White,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── Campo de opinión ──
@Composable
fun CampoOpinion(
    texto: String,
    onTextoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Card(
            colors   = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
            shape    = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value         = texto,
                onValueChange = onTextoChange,
                placeholder   = {
                    Text(
                        "¿Qué te pareció este álbum? Cuéntale a la comunidad...",
                        color    = Color.White.copy(alpha = 0.35f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                colors   = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White,
                    cursorColor             = BeatTreatColors.Purple60
                ),
                maxLines = 8
            )
        }

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, end = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text     = "${texto.length} / 500",
                color    = if (texto.length > 450) Color(0xFFFFC107)
                else Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EscribirResenaScreenPreview() {
    BeatTreatTheme {
        EscribirResenaScreenContent(
            uiState              = EscribirResenaUIState(),
            onTextoChange        = {},
            onCalificacionChange = {},
            onAlbumSeleccionado  = {},
            onBackClick          = {},
            onPublicarClick      = {}
        )
    }
}