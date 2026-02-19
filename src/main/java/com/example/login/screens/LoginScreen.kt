package com.example.login.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.login.R
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Estado del formulario (State Hoisting) ──
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val selectedTab: Int = 0
)

// ── Stateful: contiene el estado ──
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var formState by remember { mutableStateOf(LoginFormState()) }

    LoginScreenContent(
        formState         = formState,
        onEmailChange     = { formState = formState.copy(email = it) },
        onPasswordChange  = { formState = formState.copy(password = it) },
        onRememberMeChange = { formState = formState.copy(rememberMe = it) },
        onTabChange       = { tab ->
            formState = formState.copy(selectedTab = tab)
            if (tab == 1) onSignUpClick()
        },
        onLoginClick          = onLoginClick,
        onForgotPasswordClick = onForgotPasswordClick
    )
}

// ── Stateless: solo recibe datos y emite eventos ──
@Composable
fun LoginScreenContent(
    formState: LoginFormState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onTabChange: (Int) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo + Nombre ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_beattreat),
                    contentDescription = "Logo BeatTreat",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "BeatTreat",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Tabs Sign In / Sign Up ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabItem(texto = "Sign In", seleccionado = formState.selectedTab == 0, onClick = { onTabChange(0) })
                TabItem(texto = "Sign Up", seleccionado = formState.selectedTab == 1, onClick = { onTabChange(1) })
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Campo Email ──
            TextField(
                value = formState.email,
                onValueChange = onEmailChange,
                placeholder = { Text("Email", color = BeatTreatColors.TextGray) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = BeatTreatColors.FieldBackground,
                    unfocusedContainerColor = BeatTreatColors.FieldBackground,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = BeatTreatColors.TextDark,
                    unfocusedTextColor      = BeatTreatColors.TextDark,
                    cursorColor             = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Campo Password ──
            TextField(
                value = formState.password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Password", color = BeatTreatColors.TextGray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = BeatTreatColors.FieldBackground,
                    unfocusedContainerColor = BeatTreatColors.FieldBackground,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = BeatTreatColors.TextDark,
                    unfocusedTextColor      = BeatTreatColors.TextDark,
                    cursorColor             = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Checkbox Recuerda mi usuario ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recuerda mi usuario",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = formState.rememberMe,
                    onCheckedChange = onRememberMeChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor   = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onBackground,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botón Iniciar Sesión ──
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Olvidaste tu contraseña ?",
                color = BeatTreatColors.TextGray,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }
    }
}

// ── Componente reutilizable Tab ──
@Composable
fun TabItem(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Color.White else BeatTreatColors.TextGray,
            fontSize = 18.sp,
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
    BeatTreatTheme { LoginScreen() }
}