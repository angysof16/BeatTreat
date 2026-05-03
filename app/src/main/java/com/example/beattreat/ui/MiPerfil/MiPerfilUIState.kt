package com.example.beattreat.ui.MiPerfil

data class MiPerfilUIState(
    val misResenas: List<MiResenaUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // ── Estado del diálogo crear/editar ──
    val mostrarFormulario: Boolean = false,
    val resenaEnEdicion: MiResenaUI? = null,   // null = modo CREAR, not-null = modo EDITAR
    val formularioAlbumId: Int = 0,
    val formularioRating: Float = 0f,
    val formularioContent: String = "",

    // ── Diálogo de confirmación eliminar ──
    val mostrarConfirmarEliminar: Boolean = false,
    val resenaAEliminar: MiResenaUI? = null,

    // ── Cierre de sesión ──
    val cerrarSesionExitoso: Boolean = false
)
