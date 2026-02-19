package com.example.login.model

import com.example.login.R

// ── Entidades UI para Descubre ──

data class CategoriaUI(
    val id: Int,
    val nombre: String,
    val colorFondo: Long,
    val iconoRes: Int
)

data class GeneroUI(
    val id: Int,
    val nombre: String,
    val colorChip: Long
)

data class AlbumDescubreUI(
    val id: Int,
    val nombre: String,
    val artista: String,
    val imagenRes: Int
)

// ── Datos locales quemados de Descubre ──
object DescubreData {

    val categorias = listOf(
        CategoriaUI(
            id = 1,
            nombre = "Nuevos\nlanzamientos",
            colorFondo = 0xFF6366F1,
            iconoRes = 0 // R.drawable.ic_nuevos
        ),
        CategoriaUI(
            id = 2,
            nombre = "Géneros y\nEstadísticas",
            colorFondo = 0xFF8B5CF6,
            iconoRes = 0 // R.drawable.ic_generos
        ),
        CategoriaUI(
            id = 3,
            nombre = "Rankings",
            colorFondo = 0xFF10B981,
            iconoRes = 0 // R.drawable.ic_rankings
        ),
        CategoriaUI(
            id = 4,
            nombre = "Podcasts",
            colorFondo = 0xFFEC4899,
            iconoRes = 0
        )
    )

    val generos = listOf(
        GeneroUI(1, "Clásica", 0xFF84CC16),
        GeneroUI(2, "Pop latino", 0xFFEC4899),
        GeneroUI(3, "Funk", 0xFFDC2626),
        GeneroUI(4, "Hip-Hop", 0xFF06B6D4),
        GeneroUI(5, "Bachata", 0xFFF97316),
        GeneroUI(6, "Urbano", 0xFFEF4444),
        GeneroUI(7, "Rock", 0xFF9333EA),
        GeneroUI(8, "Jazz", 0xFF14B8A6)
    )

    val nuevosLanzamientos = listOf(
        AlbumDescubreUI(1, "Midnights", "Taylor Swift", 0),
        AlbumDescubreUI(2, "Renaissance", "Beyoncé", 0),
        AlbumDescubreUI(3, "Un Verano Sin Ti", "Bad Bunny", 0),
        AlbumDescubreUI(4, "Harry's House", "Harry Styles", 0),
        AlbumDescubreUI(5, "Dawn FM", "The Weeknd", 0)
    )
}