package com.example.beattreat.ui.AlbumDetalle

import com.example.beattreat.ui.Resena.ResenaDetalladaUI

data class AlbumDetalleUIState(
    val album: AlbumDetalleUI? = null,
    val esFavorito: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resenas: List<ResenaDetalladaUI> = emptyList(),
    val resenasLoading: Boolean = false,
    val resenasError: String? = null,
    val firestoreAlbumId: String = "",
    val mostrarDialogoEditar: Boolean = false,
    val resenaEditando: ResenaDetalladaUI? = null,
    val editRating: Float = 0f,
    val editContent: String = "",
    val editGuardando: Boolean = false
)