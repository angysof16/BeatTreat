package com.example.login.Grupos

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.login.Grupos.GrupoChatUI
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Stateful ──
@Composable
fun GruposScreen(
    onGrupoClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GruposViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    GruposScreenContent(
        uiState        = uiState,
        onGrupoClick   = onGrupoClick,
        onSearchClick  = onSearchClick,
        onProfileClick = onProfileClick,
        modifier       = modifier
    )
}

// ── Stateless ──
@Composable
fun GruposScreenContent(
    uiState: GruposUIState,
    onGrupoClick: () -> Unit,
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── TopBar igual al resto de la app ──
        TopBarGrupos(onSearchClick = onSearchClick, onProfileClick = onProfileClick)

        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text       = "Grupos",
                            color      = Color.White,
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text     = "${uiState.grupos.size} comunidades activas",
                            color    = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BeatTreatColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.Group,
                            contentDescription = "Grupos",
                            tint               = BeatTreatColors.Purple60,
                            modifier           = Modifier.size(26.dp)
                        )
                    }
                }
            }

            items(uiState.grupos) { grupo ->
                GrupoCard(grupo = grupo, onClick = onGrupoClick)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── TopBar consistente con el resto de la app ──
@Composable
fun TopBarGrupos(
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "BeatTreat",
                color      = Color.White,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = JaroFont,
                modifier   = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint               = Color.White,
                        modifier           = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = "Perfil",
                        tint               = Color.White,
                        modifier           = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// ── Card de Grupo ──
@Composable
fun GrupoCard(
    grupo: GrupoChatUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BeatTreatColors.SurfaceVariant)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(grupo.color, grupo.color.copy(alpha = 0.3f))
                    )
                )
                .align(Alignment.CenterStart)
        )

        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(grupo.color, grupo.color.copy(alpha = 0.5f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = grupo.nombre.take(1).uppercase(),
                    color      = Color.White,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = grupo.nombre,
                    color      = Color.White,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text     = grupo.ultimoMensaje,
                    color    = Color.White.copy(alpha = 0.55f),
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text     = grupo.hora,
                color    = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GruposScreenPreview() {
    BeatTreatTheme {
        GruposScreenContent(
            uiState      = GruposUIState(),
            onGrupoClick = {}
        )
    }
}