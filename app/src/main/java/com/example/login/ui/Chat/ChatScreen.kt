package com.example.login.ui.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Stateful ──
@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
    onProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ChatScreenContent(
        uiState         = uiState,
        onMensajeChange = { viewModel.onMensajeChange(it) },
        onEnviarClick   = { viewModel.enviarMensaje() },
        onBackClick     = onBackClick,
        modifier        = modifier
    )
}

// ── Stateless ──
@Composable
fun ChatScreenContent(
    uiState: ChatUIState,
    onMensajeChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarChat(nombreGrupo = uiState.nombreGrupo, onBackClick = onBackClick)

        LazyColumn(
            state               = listState,
            modifier            = Modifier.weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding      = PaddingValues(vertical = 16.dp)
        ) {
            items(uiState.mensajes) { mensaje ->
                if (mensaje.esPropio) MensajePropio(mensaje) else MensajeOtro(mensaje)
            }
        }

        InputMensaje(texto = uiState.mensajeTexto, onTextoChange = onMensajeChange, onEnviarClick = onEnviarClick)
    }
}

@Composable
fun TopBarChat(nombreGrupo: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Icon(Icons.Filled.Group, contentDescription = "Grupo", tint = Color.White, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(nombreGrupo, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        IconButton(onClick = {}) { Icon(Icons.Filled.VideoCall, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(28.dp)) }
        IconButton(onClick = {}) { Icon(Icons.Filled.Call,      contentDescription = "Llamar", tint = Color.White, modifier = Modifier.size(28.dp)) }
    }
}

@Composable
fun InputMensaje(texto: String, onTextoChange: (String) -> Unit, onEnviarClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value         = texto,
            onValueChange = onTextoChange,
            placeholder   = { Text("Mensaje", color = BeatTreatColors.TextGray) },
            modifier      = Modifier.weight(1f).clip(RoundedCornerShape(28.dp)),
            colors        = TextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).clickable(enabled = texto.isNotBlank()) { onEnviarClick() },
            contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
fun AvatarChat(contentDescription: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.AccountCircle, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(36.dp))
    }
}

@Composable
fun MensajeOtro(mensaje: MensajeUI, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.Bottom, modifier = modifier.fillMaxWidth()) {
        AvatarChat(contentDescription = mensaje.autor)
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.widthIn(max = 260.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(Color(0xFFE0E0E0)).padding(12.dp)
        ) {
            Column {
                // Imagen del mensaje usando AsyncImage
                if (mensaje.tieneImagen && mensaje.imagenUrl.isNotBlank()) {
                    AsyncImage(
                        model              = mensaje.imagenUrl,
                        contentDescription = "Imagen del mensaje",
                        modifier           = Modifier.size(180.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale       = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (mensaje.texto.isNotEmpty()) {
                    Text(mensaje.texto, color = BeatTreatColors.TextDark, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(mensaje.hora, color = BeatTreatColors.TextGray, fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun MensajePropio(mensaje: MensajeUI, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.End, modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.widthIn(max = 260.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(MaterialTheme.colorScheme.primary).padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(mensaje.texto, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(mensaje.hora, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        AvatarChat(contentDescription = "Yo")
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    BeatTreatTheme {
        ChatScreenContent(uiState = ChatUIState(), onMensajeChange = {}, onEnviarClick = {}, onBackClick = {})
    }
}