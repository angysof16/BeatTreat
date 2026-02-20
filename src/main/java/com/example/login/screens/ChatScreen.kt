package com.example.login.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.model.MensajeUI
import com.example.login.model.MensajesData
import com.example.login.ui.theme.BeatTreatColors
import com.example.login.ui.theme.BeatTreatTheme

// ── Estado de ChatScreen (State Hoisting) ──
data class ChatState(
    val mensajes: List<MensajeUI> = MensajesData.mensajesQueen,
    val mensajeTexto: String = "",
    val nombreGrupo: String = "Queen"
)

// ── Stateful ──
@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(ChatState()) }

    ChatScreenContent(
        state = state,
        onMensajeChange = { state = state.copy(mensajeTexto = it) },
        onEnviarClick = {
            if (state.mensajeTexto.isNotBlank()) {
                state = state.copy(mensajeTexto = "")
            }
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

// ── Stateless ──
@Composable
fun ChatScreenContent(
    state: ChatState,
    onMensajeChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBarChat(nombreGrupo = state.nombreGrupo, onBackClick = onBackClick)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(state.mensajes) { mensaje ->
                if (mensaje.esPropio) MensajePropio(mensaje) else MensajeOtro(mensaje)
            }
        }

        InputMensaje(
            texto = state.mensajeTexto,
            onTextoChange = onMensajeChange,
            onEnviarClick = onEnviarClick
        )
    }
}

// ── TopBar ──
@Composable
fun TopBarChat(
    nombreGrupo: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Icon(Icons.Filled.Group, contentDescription = "Grupo", tint = Color.White, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = nombreGrupo, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(Icons.Filled.VideoCall, contentDescription = "Videollamada", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Call, contentDescription = "Llamar", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

// ── Input de Mensaje ──
@Composable
fun InputMensaje(
    texto: String,
    onTextoChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = texto,
            onValueChange = onTextoChange,
            placeholder = { Text(text = "Mensaje", color = BeatTreatColors.TextGray) },
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(28.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
                cursorColor             = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(10.dp))
        EnviarButton(enabled = texto.isNotBlank(), onClick = onEnviarClick)
    }
}

// ── Botón Enviar──
@Composable
fun EnviarButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.Filled.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(26.dp))
    }
}

// ── Avatar de chat ──
@Composable
fun AvatarChat(
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(36.dp))
    }
}

// ── Mensaje de Otro Usuario ──
@Composable
fun MensajeOtro(
    mensaje: MensajeUI,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.Bottom, modifier = modifier.fillMaxWidth()) {
        AvatarChat(contentDescription = mensaje.autor)
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(Color(0xFFE0E0E0))
                .padding(12.dp)
        ) {
            Column {
                MensajeImagenOtro(mensaje = mensaje)
                if (mensaje.texto.isNotEmpty()) {
                    Text(text = mensaje.texto, color = BeatTreatColors.TextDark, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(text = mensaje.hora, color = BeatTreatColors.TextGray, fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

// ── Imagen dentro de burbuja ajena ──
@Composable
fun MensajeImagenOtro(
    mensaje: MensajeUI,
    modifier: Modifier = Modifier
) {
    if (!mensaje.tieneImagen) return
    if (mensaje.imagenRes != null && mensaje.imagenRes != 0) {
        Image(
            painter = painterResource(id = mensaje.imagenRes),
            contentDescription = "Imagen del mensaje",
            modifier = modifier.size(180.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier = modifier.size(180.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray.copy(alpha = 0.4f)))
    }
    Spacer(modifier = Modifier.height(4.dp))
}

// ── Mensaje Propio ──
@Composable
fun MensajePropio(
    mensaje: MensajeUI,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(text = mensaje.texto, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = mensaje.hora, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        AvatarChat(contentDescription = "Yo")
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    BeatTreatTheme { ChatScreen() }
}