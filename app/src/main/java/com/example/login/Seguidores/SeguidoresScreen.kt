package com.example.login.Seguidores

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun SeguidoresScreen(
    tipo: String,                          // "siguiendo" | "seguidores"
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SeguidoresViewModel
) {
    LaunchedEffect(tipo) { viewModel.cargar(tipo) }

    val uiState by viewModel.uiState.collectAsState()

    SeguidoresScreenContent(
        uiState     = uiState,
        onBackClick = onBackClick,
        onToggleSeguir = { id -> viewModel.toggleSeguir(id) },
        modifier    = modifier
    )
}

// ── Stateless ──
@Composable
fun SeguidoresScreenContent(
    uiState: SeguidoresUIState,
    onBackClick: () -> Unit,
    onToggleSeguir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // ── TopBar ──
        Row(
            modifier          = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Text(
                text       = if (uiState.tipo == "siguiendo") "Siguiendo" else "Seguidores",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.usuarios.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text     = if (uiState.tipo == "siguiendo") "Aún no sigues a nadie" else "Aún no tienes seguidores",
                            color    = Color.White.copy(alpha = 0.4f),
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(uiState.usuarios) { usuario ->
                    UsuarioItem(
                        usuario        = usuario,
                        esSiguiendo    = usuario.id in uiState.siguiendoIds,
                        onSeguirClick  = { onToggleSeguir(usuario.id) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
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
        modifier          = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(BeatTreatColors.SurfaceVariant).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(48.dp).clip(CircleShape).background(BeatTreatColors.Purple40),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = usuario.nombre,   color = Color.White,                    fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = usuario.usuario,  color = Color.White.copy(alpha = 0.55f), fontSize = 13.sp)
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