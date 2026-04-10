package com.example.login.ui.EscribirResena

import com.example.login.data.dto.AlbumDto

data class EscribirResenaUIState(
    val textoResena: String = "",
    val calificacion: Float = 0f,
    val albumSeleccionado: String = "",
    val albumId: Int = 0,
    val publicadoExitoso: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Lista de álbumes cargados desde el backend
    val albumesBackend: List<AlbumDto> = emptyList(),
    val albumesCargando: Boolean = false
)