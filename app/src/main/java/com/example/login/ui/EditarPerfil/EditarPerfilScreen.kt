package com.example.login.ui.EditarPerfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.login.R
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

private val JaroFont = FontFamily(Font(R.font.jaro_regular, FontWeight.Normal))

// ── Stateful ──
@Composable
fun EditarPerfilScreen(
    onBackClick: () -> Unit = {},
    onGuardarClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: EditarPerfilViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // ── Launcher de galería ──────────────────────────────────────────────────
    // Cuando el usuario elige una imagen se llama subirFotoPerfil() en el VM
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.subirFotoPerfil(it) }
    }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onGuardarClick()
            viewModel.resetGuardado()
        }
    }

    EditarPerfilScreenContent(
        uiState            = uiState,
        onBackClick        = onBackClick,
        onNombreChange     = { viewModel.onNombreChange(it) },
        onUsuarioChange    = { viewModel.onUsuarioChange(it) },
        onBioChange        = { viewModel.onBioChange(it) },
        onGuardarClick     = { viewModel.guardar() },
        // Al tocar la foto o el botón de cámara se abre la galería
        onCambiarFotoClick = { galleryLauncher.launch("image/*") },
        modifier           = modifier
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
    onCambiarFotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarEditarPerfil(onBackClick = onBackClick, onGuardarClick = onGuardarClick)

        Column(
            modifier            = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── Avatar con botón de cámara ──────────────────────────────────
            // Toda el área del avatar es clickable para abrir la galería
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier         = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(BeatTreatColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    // Si hay URL muestra la foto con Coil, si no el ícono genérico
                    if (uiState.fotoPerfilUrl.isNotBlank()) {
                        AsyncImage(
                            model              = uiState.fotoPerfilUrl,
                            contentDescription = "Foto de perfil",
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop,
                            error              = painterResource(R.drawable.foto_perfil),
                            placeholder        = painterResource(R.drawable.foto_perfil)
                        )
                    } else {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(90.dp)
                        )
                    }

                    // Overlay de carga mientras se sube la foto
                    if (uiState.isUploadingPhoto) {
                        Box(
                            modifier         = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color       = BeatTreatColors.Purple60,
                                modifier    = Modifier.size(32.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                // Botón cámara en la esquina — abre galería
                Box(
                    modifier         = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(BeatTreatColors.Purple60),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick  = onCambiarFotoClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint               = Color.White,
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto "Cambiar foto" también es clickable
            Text(
                text     = if (uiState.isUploadingPhoto) "Subiendo foto..." else "Cambiar foto de perfil",
                color    = BeatTreatColors.Purple60,
                fontSize = 14.sp,
                modifier = if (!uiState.isUploadingPhoto) Modifier.clickableIfEnabled(onCambiarFotoClick) else Modifier
            )

            // Mensaje de error si falla la subida
            uiState.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = msg, color = BeatTreatColors.Error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Campos de texto ──
            CampoEditable(label = "Nombre",  valor = uiState.nombre,  onValueChange = onNombreChange,  icono = Icons.Filled.Person)
            Spacer(modifier = Modifier.height(16.dp))
            CampoEditable(label = "Usuario", valor = uiState.usuario, onValueChange = onUsuarioChange, icono = Icons.Filled.AlternateEmail, prefijo = "@")
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Biografía", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
            Card(colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = uiState.bio, onValueChange = onBioChange,
                    placeholder = { Text("Cuéntale algo a la comunidad...", color = Color.White.copy(alpha = 0.35f)) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = BeatTreatColors.Purple60
                    ),
                    maxLines = 5
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick  = onGuardarClick,
                enabled  = !uiState.isUploadingPhoto,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = BeatTreatColors.Purple60)
            ) {
                Text("Guardar cambios", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Extensión helper para hacer un Modifier clickable condicionalmente
private fun Modifier.clickableIfEnabled(onClick: () -> Unit): Modifier =
    this.then(Modifier.clickable{ onClick() })

// ── TopBar ──
@Composable
fun TopBarEditarPerfil(
    onBackClick: () -> Unit,
    onGuardarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier         = Modifier.size(80.dp).clip(RoundedCornerShape(bottomEnd = 12.dp)).background(Color(0xFF1A1A1A)),
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
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(bottomStart = 12.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Text(text = "BeatTreat", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Normal, fontFamily = JaroFont,
                modifier = Modifier.weight(1f).padding(start = 4.dp))
            TextButton(onClick = onGuardarClick) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// ── Campo editable reutilizable ──
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
        Card(colors = CardDefaults.cardColors(containerColor = BeatTreatColors.SurfaceVariant), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = valor, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = BeatTreatColors.Purple60
                ),
                singleLine  = true,
                leadingIcon = { Icon(icono, contentDescription = null, tint = Color.White.copy(alpha = 0.5f)) },
                prefix      = if (prefijo.isNotEmpty()) { { Text(prefijo, color = Color.White.copy(alpha = 0.5f)) } } else null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarPerfilScreenPreview() {
    BeatTreatTheme {
        EditarPerfilScreenContent(
            uiState            = EditarPerfilUIState(nombre = "Alex Morrison", usuario = "alexmrrsn"),
            onBackClick        = {},
            onNombreChange     = {},
            onUsuarioChange    = {},
            onBioChange        = {},
            onGuardarClick     = {},
            onCambiarFotoClick = {}
        )
    }
}