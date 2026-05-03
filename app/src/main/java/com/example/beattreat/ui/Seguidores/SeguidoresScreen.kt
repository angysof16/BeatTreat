package com.example.beattreat.ui.Seguidores

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beattreat.R
import com.example.beattreat.ui.theme.BeatTreatColors
import com.example.beattreat.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

@Composable
fun SeguidoresScreen(
    tipo: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SeguidoresViewModel
) {
    LaunchedEffect(tipo) { viewModel.cargar(tipo) }
    val uiState by viewModel.uiState.collectAsState()
    SeguidoresScreenContent(
        uiState        = uiState,
        onBackClick    = onBackClick,
        onToggleSeguir = { firestoreId -> viewModel.toggleSeguir(firestoreId) },  // ← pasar firestoreId
        modifier       = modifier
    )
}

@Composable
fun SeguidoresScreenContent(
    uiState: SeguidoresUIState,
    onBackClick: () -> Unit,
    onToggleSeguir: (String) -> Unit,  // ← ahora recibe String (firestoreId)
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarSeguidores(tipo = uiState.tipo, onBackClick = onBackClick)

        // FIX: muestra spinner mientras carga desde Firestore
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BeatTreatColors.Purple60)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Cargando...",
                            color    = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.WifiOff, null,
                            tint     = BeatTreatColors.Error.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            uiState.errorMessage,
                            color    = BeatTreatColors.Error,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text       = if (uiState.tipo == "siguiendo") "Siguiendo" else "Seguidores",
                            color      = Color.White,
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (uiState.usuarios.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        if (uiState.tipo == "siguiendo") Icons.Filled.PersonSearch
                                        else Icons.Filled.Group,
                                        contentDescription = null,
                                        tint     = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text  = if (uiState.tipo == "siguiendo")
                                            "Aún no sigues a nadie"
                                        else
                                            "Aún no tienes seguidores",
                                        color    = Color.White.copy(alpha = 0.4f),
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.usuarios) { usuario ->
                            UsuarioItem(
                                usuario       = usuario,
                                esSiguiendo   = usuario.firestoreId in uiState.siguiendoIds,
                                onSeguirClick = { onToggleSeguir(usuario.firestoreId) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun TopBarSeguidores(tipo: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
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
                    shape = RoundedCornerShape(bottomStart = 12.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Text(
                "BeatTreat", color = Color.White, fontSize = 28.sp,
                fontWeight = FontWeight.Normal, fontFamily = JaroFont,
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
            Text(
                text     = if (tipo == "siguiendo") "Siguiendo" else "Seguidores",
                color    = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun UsuarioItem(
    usuario: UsuarioUI,
    esSiguiendo: Boolean,
    onSeguirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BeatTreatColors.SurfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(BeatTreatColors.Purple40),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AccountCircle, null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(usuario.nombre,  color = Color.White,                     fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(usuario.usuario, color = Color.White.copy(alpha = 0.55f), fontSize = 13.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (esSiguiendo) BeatTreatColors.SurfaceVariant else BeatTreatColors.Purple60)
                .clickable { onSeguirClick() }
                .padding(horizontal = 16.dp, vertical = 7.dp)
        ) {
            Text(
                text       = if (esSiguiendo) "Siguiendo" else "Seguir",
                color      = Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeguidoresScreenPreview() {
    BeatTreatTheme {
        SeguidoresScreenContent(
            uiState        = SeguidoresUIState(tipo = "siguiendo"),
            onBackClick    = {},
            onToggleSeguir = {}
        )
    }
}
