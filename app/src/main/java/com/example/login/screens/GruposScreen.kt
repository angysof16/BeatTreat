package com.example.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.data.GrupoChatData
import com.example.login.model.GrupoChatUI

@Composable
fun GruposScreen(
    onGrupoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E16))
    ) {

        TopBarProfile(onSearchClick = {})

        Text(
            text = "Grupos de Bandas",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(GrupoChatData.grupos) { grupo ->
                GrupoItem(
                    grupo = grupo,
                    onClick = onGrupoClick
                )
            }
        }
    }
}

@Composable
fun GrupoItem(
    grupo: GrupoChatUI,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }   // 🔥 AQUÍ ESTÁ LA NAVEGACIÓN
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(grupo.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Grupo",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = grupo.nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = grupo.ultimoMensaje,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            Text(
                text = grupo.hora,
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Divider(color = Color.Gray.copy(alpha = 0.3f))
    }
}

@Preview(showBackground = true)
@Composable
fun GruposScreenPreview() {
    GruposScreen()
}