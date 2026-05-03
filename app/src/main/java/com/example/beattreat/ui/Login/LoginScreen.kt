package com.example.beattreat.ui.Login

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beattreat.ui.Home.JaroFont
import com.example.beattreat.R
import com.example.beattreat.ui.theme.BeatTreatColors
import com.example.beattreat.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun LoginScreen(
    onForgotPasswordClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreenContent(
        uiState               = uiState,
        onEmailChange         = { viewModel.onEmailChange(it) },
        onPasswordChange      = { viewModel.onPasswordChange(it) },
        onRememberMeChange    = { viewModel.onRememberMeChange(it) },
        onTabChange           = { viewModel.onTabChange(it) },
        onLoginClick          = { viewModel.login() },
        onForgotPasswordClick = onForgotPasswordClick,
        modifier              = modifier
    )
}

// ── Stateless ──
@Composable
fun LoginScreenContent(
    uiState: LoginUIState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onTabChange: (Int) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier              = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center
        ) {
            LogoBeatTreat()
            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TabItem(texto = "Sign In", seleccionado = uiState.selectedTab == 0, onClick = { onTabChange(0) })
                TabItem(texto = "Sign Up", seleccionado = uiState.selectedTab == 1, onClick = { onTabChange(1) })
            }

            Spacer(modifier = Modifier.height(36.dp))

            TextField(
                value         = uiState.email,
                onValueChange = onEmailChange,
                placeholder   = { Text("Email", color = BeatTreatColors.TextGray) },
                modifier      = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                colors        = campoColores(),
                singleLine    = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value                 = uiState.password,
                onValueChange         = onPasswordChange,
                placeholder           = { Text("Password", color = BeatTreatColors.TextGray) },
                visualTransformation  = PasswordVisualTransformation(),
                modifier              = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                colors                = campoColores(),
                singleLine            = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            RecuerdameRow(checked = uiState.rememberMe, onCheckedChange = onRememberMeChange)

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
                onClick  = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(28.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text     = "Olvidaste tu contraseña ?",
                color    = BeatTreatColors.TextGray,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }
    }
}

// ── Logo BeatTreat ──
@Composable
fun LogoBeatTreat(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.logo_beattreat), contentDescription = "Logo BeatTreat", modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = "BeatTreat", color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = JaroFont)
    }
}

// ── Fila Recuérdame ──
@Composable
fun RecuerdameRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        Text(text = "Recuerda mi usuario", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = CheckboxDefaults.colors(
                checkedColor   = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onBackground,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

// ── Colores comunes de campo de texto ──
@Composable
private fun campoColores() = TextFieldDefaults.colors(
    focusedContainerColor   = BeatTreatColors.FieldBackground,
    unfocusedContainerColor = BeatTreatColors.FieldBackground,
    focusedIndicatorColor   = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTextColor        = BeatTreatColors.TextDark,
    unfocusedTextColor      = BeatTreatColors.TextDark,
    cursorColor             = MaterialTheme.colorScheme.primary
)

// ── Tab reutilizable ──
@Composable
fun TabItem(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = modifier.clickable { onClick() }
    ) {
        Text(
            text       = texto,
            color      = if (seleccionado) Color.White else BeatTreatColors.TextGray,
            fontSize   = 18.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (seleccionado) BeatTreatColors.Purple60 else Color.Transparent)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BeatTreatTheme {
        LoginScreenContent(
            uiState               = LoginUIState(errorMessage = "Credenciales incorrectas"),
            onEmailChange         = {},
            onPasswordChange      = {},
            onRememberMeChange    = {},
            onTabChange           = {},
            onLoginClick          = {},
            onForgotPasswordClick = {}
        )
    }
}