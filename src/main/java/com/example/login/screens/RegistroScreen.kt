package com.example.login.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
data class RegistroFormState(
    val email: String = "",
    val password: String = "",
    val selectedTab: Int = 1
)

// ── Stateful ──
@Composable
fun RegistroScreen(
    onRegistroClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(RegistroFormState()) }

    RegistroScreenContent(
        formState = formState,
        onEmailChange = { formState = formState.copy(email = it) },
        onPasswordChange = { formState = formState.copy(password = it) },
        onTabChange = { tab ->
            formState = formState.copy(selectedTab = tab)
            if (tab == 0) onSignInClick()
        },
        onRegistroClick = onRegistroClick,
        onGoogleClick = onGoogleClick,
        modifier = modifier
    )
}

// ── Stateless ──
@Composable
fun RegistroScreenContent(
    formState: RegistroFormState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo ──
            LogoBeatTreat()

            Spacer(modifier = Modifier.height(40.dp))

            // ── Tabs ──
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TabItem(texto = "Sign In", seleccionado = formState.selectedTab == 0, onClick = { onTabChange(0) })
                TabItem(texto = "Sign Up", seleccionado = formState.selectedTab == 1, onClick = { onTabChange(1) })
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Campos ──
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

            Spacer(modifier = Modifier.height(28.dp))

            // ── Botón Regístrate ──
            Button(
                onClick = onRegistroClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Regístrate", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Botón Google ──
            GoogleSignUpButton(onClick = onGoogleClick)
        }
    }
}

// ── Botón Google extraído ──
@Composable
fun GoogleSignUpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text(text = "Sign up with Google", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroScreenPreview() {
    BeatTreatTheme { RegistroScreen() }
}