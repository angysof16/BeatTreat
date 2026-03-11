package com.example.login.Comentarios

import com.example.login.Resena.ComentarioUI
import com.example.login.Resena.ResenaDetalladaUI

data class ComentariosUIState(
    val resena: ResenaDetalladaUI? = null,
    val comentarios: List<ComentarioUI> = emptyList(),
    val nuevoComentario: String = "",
    val comentariosLikeados: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)