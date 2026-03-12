package com.example.login.EditarPerfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun EditarPerfilScreen(
    onBackClick: () -> Unit = {},
    onGuardarClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: EditarPerfilViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onGuardarClick()
            viewModel.resetGuardado()
        }
    }

    EditarPerfilScreenContent(
        uiState           = uiState,
        onBackClick       = onBackClick,
        onNombreChange    = { viewModel.onNombreChange(it) },
        onUsuarioChange   = { viewModel.onUsuarioChange(it) },
        onBioChange       = { viewModel.onBioChange(it) },
        onGuardarClick    = { viewModel.guardar() },
        modifier          = modifier
    )
}

// ── Stateless ──
@Composable
fun EditarPerfilScreenContent(
    uiState: EditarPerfilUIState,
    onBackClick: () -> Unit,
    onNombreChange: (String) -> Unit,
    onUsuarioChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
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
                text       = "Editar perfil",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.weight(1f).padding(start = 8.dp)
            )
            TextButton(onClick = onGuardarClick) {
                Text(text = "Guardar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Column(
            modifier            = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── Avatar con botón editar ──
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier         = Modifier.size(100.dp).clip(CircleShape).background(BeatTreatColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(90.dp))
                }
                Box(
                    modifier         = Modifier.size(30.dp).clip(CircleShape).background(BeatTreatColors.Purple60),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Cambiar foto de perfil", color = BeatTreatColors.Purple60, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // ── Campos ──
            CampoEditable(
                label         = "Nombre",
                valor         = uiState.nombre,
                onValueChange = onNombreChange,
                icono         = Icons.Filled.Person
            )
            Spacer(modifier = Modifier.height(16.dp))
            CampoEditable(
                label         = "Usuario",
                valor         = uiState.usuario,
                onValueChange = onUsuarioChange,
                icono         = Icons.Filled.AlternateEmail,
                prefijo       = "@"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo bio multilínea
            Text(text = "Biografía", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
                shape  = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value         = uiState.bio,
                    onValueChange = onBioChange,
                    placeholder   = { Text("Cuéntale algo a la comunidad...", color = Color.White.copy(alpha = 0.35f)) },
                    modifier      = Modifier.fillMaxWidth().height(120.dp),
                    colors        = TextFieldDefaults.colors(
                        focusedContainerColor   = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor        = Color.White,
                        unfocusedTextColor      = Color.White,
                        cursorColor             = BeatTreatColors.Purple60
                    ),
                    maxLines = 5
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick  = onGuardarClick,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
            ) {
                Text("Guardar cambios", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CampoEditable(
    label: String,
    valor: String,
    onValueChange: (String) -> Unit,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    prefijo: String = "",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant),
            shape  = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value         = valor,
                onValueChange = onValueChange,
                modifier      = Modifier.fillMaxWidth(),
                colors        = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White,
                    cursorColor             = BeatTreatColors.Purple60
                ),
                singleLine    = true,
                leadingIcon   = { Icon(icono, contentDescription = null, tint = Color.White.copy(alpha = 0.5f)) },
                prefix        = if (prefijo.isNotEmpty()) { { Text(prefijo, color = Color.White.copy(alpha = 0.5f)) } } else null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarPerfilScreenPreview() {
    BeatTreatTheme {
        EditarPerfilScreenContent(
            uiState         = EditarPerfilUIState(nombre = "Alex Morrison", usuario = "alexmrrsn"),
            onBackClick     = {},
            onNombreChange  = {},
            onUsuarioChange = {},
            onBioChange     = {},
            onGuardarClick  = {}
        )
    }
}