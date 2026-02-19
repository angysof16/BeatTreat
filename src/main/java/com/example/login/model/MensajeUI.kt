package com.example.login.model

import com.example.login.R

// ── Entidad de UI que representa un mensaje en pantalla ──
data class MensajeUI(
    val id: Int,
    val texto: String,
    val autor: String,
    val hora: String,
    val esPropio: Boolean,
    val tieneImagen: Boolean = false,
    val imagenRes: Int? = null
)

// ── Datos locales quemados ──
object MensajesData {
    val mensajesQueen = listOf(
        MensajeUI(
            id = 1,
            texto = "Nuevo diseño de album :)",
            autor = "Freddie",
            hora = "3:20 p.m",
            esPropio = false
        ),
        MensajeUI(
            id = 2,
            texto = "",
            autor = "Freddie",
            hora = "3:22 p.m",
            esPropio = false,
            tieneImagen = true,
            imagenRes = R.drawable.ejemplo_queen
        ),
        MensajeUI(
            id = 3,
            texto = "Wooooooooow\nla mejor banda, esperando\npor el proximo album.\ncuando sacaran gira ?",
            autor = "Brian",
            hora = "3:45 p.m",
            esPropio = false
        ),
        MensajeUI(
            id = 4,
            texto = "Gracias quedo genial",
            autor = "Yo",
            hora = "3:50 p.m",
            esPropio = true
        ),
        MensajeUI(
            id = 5,
            texto = "La gira empieza en julio!",
            autor = "Roger",
            hora = "3:55 p.m",
            esPropio = false
        ),
        MensajeUI(
            id = 6,
            texto = "No me lo puedo perder ",
            autor = "Yo",
            hora = "4:00 p.m",
            esPropio = true
        )
    )
}