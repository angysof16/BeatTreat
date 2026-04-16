package com.example.login.ui.EscribirResena

import com.example.login.data.dto.AlbumDto

data class EscribirResenaUIState(
    val textoResena: String = "",
    val calificacion: Float = 0f,
    val albumSeleccionado: String = "",
    val albumId: Int = 0,
    // true cuando se navega desde el detalle de un álbum concreto:
    // en ese caso NO se muestra el selector
    val albumFijado: Boolean = false,
    val publicadoExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val albumesBackend: List<AlbumDto> = emptyList(),
    val albumesCargando: Boolean = false
)