package com.example.beattreat.model

import com.example.beattreat.ui.Chat.MensajeUI

// ── Datos locales quemados ──
object MensajesData {
    val mensajesQueen = listOf(
        MensajeUI(
            id       = 1,
            texto    = "Nuevo diseño de album :)",
            autor    = "Freddie",
            hora     = "3:20 p.m",
            esPropio = false
        ),
        MensajeUI(
            id          = 2,
            texto       = "",
            autor       = "Freddie",
            hora        = "3:22 p.m",
            esPropio    = false,
            tieneImagen = true,
            imagenUrl   = "HTTPS://PLACEHOLDER.COM/CHAT/QUEEN_ALBUM_DESIGN.JPG"
        ),
        MensajeUI(
            id       = 3,
            texto    = "Wooooooooow\nla mejor banda, esperando\npor el proximo album.\ncuando sacaran gira ?",
            autor    = "Brian",
            hora     = "3:45 p.m",
            esPropio = false
        ),
        MensajeUI(
            id       = 4,
            texto    = "Gracias quedo genial",
            autor    = "Yo",
            hora     = "3:50 p.m",
            esPropio = true
        ),
        MensajeUI(
            id       = 5,
            texto    = "La gira empieza en julio!",
            autor    = "Roger",
            hora     = "3:55 p.m",
            esPropio = false
        ),
        MensajeUI(
            id       = 6,
            texto    = "No me lo puedo perder ",
            autor    = "Yo",
            hora     = "4:00 p.m",
            esPropio = true
        )
    )
}