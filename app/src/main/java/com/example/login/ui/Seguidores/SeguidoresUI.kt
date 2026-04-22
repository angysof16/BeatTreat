package com.example.login.ui.Seguidores

data class UsuarioUI(val id: Int, val nombre: String, val usuario: String)

/**
 * FIX: se agrega errorMessage para mostrar errores de carga desde Firestore.
 */
data class SeguidoresUIState(
    val tipo: String = "seguidores",
    val usuarios: List<UsuarioUI> = emptyList(),
    val siguiendoIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
