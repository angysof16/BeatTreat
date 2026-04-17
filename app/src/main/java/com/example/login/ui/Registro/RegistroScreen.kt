// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/Registro/RegistroScreen.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.Registro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.ui.Login.LogoBeatTreat
import com.example.login.ui.Login.TabItem
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun RegistroScreen(
    onGoogleClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    RegistroScreenContent(
        uiState          = uiState,
        onEmailChange    = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onNombreChange   = { viewModel.onNombreChange(it) },
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onCountryChange  = { viewModel.onCountryChange(it) },
        onBioChange      = { viewModel.onBioChange(it) },
        onTabChange      = { viewModel.onTabChange(it) },
        onRegistroClick  = { viewModel.registrar() },
        onGoogleClick    = onGoogleClick,
        modifier         = modifier
    )
}

// ── Stateless ──
@Composable
fun RegistroScreenContent(
    uiState: RegistroUIState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onTabChange: (Int) -> Unit,
    onRegistroClick: () -> Unit,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogoBeatTreat()
            Spacer(modifier = Modifier.height(32.dp))

            // ── Tabs Sign In / Sign Up ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabItem(texto = "Sign In", seleccionado = uiState.selectedTab == 0, onClick = { onTabChange(0) })
                TabItem(texto = "Sign Up", seleccionado = uiState.selectedTab == 1, onClick = { onTabChange(1) })
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Campos obligatorios ──
            CampoRegistro(
                value         = uiState.nombre,
                onValueChange = onNombreChange,
                placeholder   = "Nombre completo *",
                icon          = Icons.Filled.Person
            )
            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                value         = uiState.username,
                onValueChange = onUsernameChange,
                placeholder   = "Nombre de usuario *",
                icon          = Icons.Filled.AlternateEmail
            )
            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                value         = uiState.email,
                onValueChange = onEmailChange,
                placeholder   = "Email *",
                icon          = Icons.Filled.MailOutline
            )
            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                value                = uiState.password,
                onValueChange        = onPasswordChange,
                placeholder          = "Contraseña * (mín. 6 caracteres)",
                icon                 = Icons.Filled.Lock,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ── Campos opcionales ──
            CampoRegistro(
                value         = uiState.country,
                onValueChange = onCountryChange,
                placeholder   = "País (opcional)",
                icon          = Icons.Filled.Place
            )
            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                value         = uiState.bio,
                onValueChange = onBioChange,
                placeholder   = "Biografía (opcional)",
                icon          = Icons.Filled.AccountCircle,
                maxLines      = 3
            )

            // ── Mensaje de error ──
            if (!uiState.errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BeatTreatColors.Error.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text     = uiState.errorMessage,
                        color    = BeatTreatColors.Error,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick  = onRegistroClick,
                enabled  = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(28.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Regístrate",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            GoogleSignUpButton(onClick = onGoogleClick)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Campo genérico reutilizable para registro ──
@Composable
fun CampoRegistro(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    TextField(
        value                = value,
        onValueChange        = onValueChange,
        placeholder          = { Text(placeholder, color = BeatTreatColors.TextGray, fontSize = 14.sp) },
        visualTransformation = visualTransformation,
        singleLine           = maxLines == 1,
        maxLines             = maxLines,
        modifier             = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
        colors               = TextFieldDefaults.colors(
            focusedContainerColor   = BeatTreatColors.FieldBackground,
            unfocusedContainerColor = BeatTreatColors.FieldBackground,
            focusedIndicatorColor   = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor        = BeatTreatColors.TextDark,
            unfocusedTextColor      = BeatTreatColors.TextDark,
            cursorColor             = MaterialTheme.colorScheme.primary
        ),
        leadingIcon = {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = BeatTreatColors.TextGray,
                modifier           = Modifier.size(20.dp)
            )
        }
    )
}

// ── Botón Google ──
@Composable
fun GoogleSignUpButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .border(1.5.dp, BeatTreatColors.TextGray, RoundedCornerShape(28.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(text = "G", color = Color(0xFF4285F4), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text       = "Sign up with Google",
                color      = MaterialTheme.colorScheme.onBackground,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroScreenPreview() {
    BeatTreatTheme {
        RegistroScreenContent(
            uiState          = RegistroUIState(),
            onEmailChange    = {},
            onPasswordChange = {},
            onNombreChange   = {},
            onUsernameChange = {},
            onCountryChange  = {},
            onBioChange      = {},
            onTabChange      = {},
            onRegistroClick  = {},
            onGoogleClick    = {}
        )
    }
}
