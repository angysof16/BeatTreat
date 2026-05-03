package com.example.beattreat.ui.Comentarios

import com.example.beattreat.ui.Resena.ComentarioUI
import com.example.beattreat.ui.Resena.ResenaDetalladaUI

data class ComentariosUIState(
    val resena: ResenaDetalladaUI? = null,
    val comentarios: List<ComentarioUI> = emptyList(),
    val nuevoComentario: String = "",
    val comentariosLikeados: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)