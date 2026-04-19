package com.example.login.ui.AlbumDetalle

import com.example.login.ui.Resena.ResenaDetalladaUI

data class AlbumDetalleUIState(
    val album: AlbumDetalleUI? = null,
    val esFavorito: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resenas: List<ResenaDetalladaUI> = emptyList(),
    val resenasLoading: Boolean = false,
    val resenasError: String? = null,
    val firestoreAlbumId: String = ""
)