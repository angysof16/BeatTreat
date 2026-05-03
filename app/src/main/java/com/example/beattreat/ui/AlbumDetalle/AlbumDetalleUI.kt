package com.example.beattreat.ui.AlbumDetalle

// ── Entidades UI para Detalle de Álbum ──

data class AlbumDetalleUI(
    val id: Int,
    val nombre: String,
    val artista: String,
    val año: String,
    val genero: String,
    val descripcion: String,
    val canciones: List<CancionDetalleUI>,
    val imagenUrl: String,
    val duracionTotal: String,
    val calificacionPromedio: Float,
    val totalResenas: Int
)

data class CancionDetalleUI(
    val numero: Int,
    val titulo: String,
    val duracion: String
)