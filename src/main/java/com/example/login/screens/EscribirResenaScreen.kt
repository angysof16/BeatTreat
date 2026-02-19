package com.example.login.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// ── Estado del formulario (State Hoisting) ──
data class EscribirResenaState(
    val textoResena: String = "",
    val calificacion: Float = 0f,
    val albumSeleccionado: String = "" // Placeholder
)

// ── Stateful ──
@Composable
fun EscribirResenaScreen(
    onBackClick: () -> Unit = {},
    onPublicarClick: (String, Float) -> Unit = { _, _ -> }
) {
    var state by remember { mutableStateOf(EscribirResenaState()) }

    EscribirResenaScreenContent(
        state = state,
        onTextoChange = { state = state.copy(textoResena = it) },
        onCalificacionChange = { state = state.copy(calificacion = it) },
        onBackClick = onBackClick,
        onPublicarClick = {
            if (state.textoResena.isNotBlank() && state.calificacion > 0) {
                onPublicarClick(state.textoResena, state.calificacion)
            }
        }
    )
}

// ── Stateless ──
@Composable
fun EscribirResenaScreenContent(
    state: EscribirResenaState,
    onTextoChange: (String) -> Unit,
    onCalificacionChange: (Float) -> Unit,
    onBackClick: () -> Unit,
    onPublicarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TopBar
        TopBarEscribirResena(
            onBackClick = onBackClick,
            onPublicarClick = onPublicarClick,
            habilitarPublicar = state.textoResena.isNotBlank() && state.calificacion > 0
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = "Escribe tu reseña",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Seleccionar álbum (placeholder)
            Text(
                text = "Álbum",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BeatTreatColors.SurfaceVariant)
                    .clickable { /* TODO: Abrir selector de álbumes */ }
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (state.albumSeleccionado.isBlank()) "Seleccionar álbum" else state.albumSeleccionado,
                    color = if (state.albumSeleccionado.isBlank()) Color.White.copy(alpha = 0.5f) else Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calificación
            Text(
                text = "Calificación",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            CalificacionSelector(
                calificacion = state.calificacion,
                onCalificacionChange = onCalificacionChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de texto
            Text(
                text = "Tu opinión",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = state.textoResena,
                onValueChange = onTextoChange,
                placeholder = {
                    Text(
                        "¿Qué te pareció este álbum? Comparte tu opinión...",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BeatTreatColors.SurfaceVariant,
                    unfocusedContainerColor = BeatTreatColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                maxLines = 8
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contador de caracteres
            Text(
                text = "${state.textoResena.length} / 500",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// ── TopBar ──
@Composable
fun TopBarEscribirResena(
    onBackClick: () -> Unit,
    onPublicarClick: () -> Unit,
    habilitarPublicar: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Nueva Reseña",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = onPublicarClick,
            enabled = habilitarPublicar,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.White.copy(alpha = 0.3f),
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Publicar",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Selector de Calificación ──
@Composable
fun CalificacionSelector(
    calificacion: Float,
    onCalificacionChange: (Float) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { index ->
            val estrellaLlena = index < calificacion.toInt()
            IconButton(
                onClick = { onCalificacionChange((index + 1).toFloat()) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (estrellaLlena) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Estrella ${index + 1}",
                    tint = if (estrellaLlena) Color(0xFFFFC107) else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }

    if (calificacion > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (calificacion.toInt()) {
                1 -> "Muy malo"
                2 -> "Malo"
                3 -> "Regular"
                4 -> "Bueno"
                5 -> "Excelente"
                else -> ""
            },
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EscribirResenaScreenPreview() {
    BeatTreatTheme {
        EscribirResenaScreen()
    }
}