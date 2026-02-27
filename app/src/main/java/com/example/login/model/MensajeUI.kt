package com.example.login.model

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