// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/EscribirResena/EscribirResenaUIState.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.EscribirResena

import com.example.login.data.dto.AlbumDto

data class EscribirResenaUIState(
    val textoResena: String       = "",
    val calificacion: Float       = 0f,
    val albumSeleccionado: String = "",
    val albumId: Int              = 0,
    // ID real en Firestore (string) — necesario para crear el review
    val firestoreAlbumId: String  = "",
    val albumFijado: Boolean      = false,
    val publicadoExitoso: Boolean = false,
    val isLoading: Boolean        = false,
    val errorMessage: String?     = null,
    val albumesBackend: List<AlbumDto> = emptyList(),
    val albumesCargando: Boolean  = false
)
