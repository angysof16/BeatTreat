package com.example.login.model

import com.example.login.R

data class CategoriaUI(val id: Int, val nombre: String, val colorFondo: Long, val iconoRes: Int)
data class GeneroUI(val id: Int, val nombre: String, val colorChip: Long)
data class AlbumDescubreUI(val id: Int, val nombre: String, val artista: String, val imagenRes: Int)

object DescubreData {

    val categorias = listOf(
        CategoriaUI(1, "Nuevos\nlanzamientos", 0xFF6366F1, 0),
        CategoriaUI(2, "Géneros y\nEstadísticas", 0xFF8B5CF6, 0),
        CategoriaUI(3, "Rankings",  0xFF10B981, 0),
        CategoriaUI(4, "Podcasts",  0xFFEC4899, 0)
    )

    val generos = listOf(
        GeneroUI(1, "Clásica",    0xFF84CC16),
        GeneroUI(2, "Pop latino", 0xFFEC4899),
        GeneroUI(3, "Funk",       0xFFDC2626),
        GeneroUI(4, "Hip-Hop",    0xFF06B6D4),
        GeneroUI(5, "Bachata",    0xFFF97316),
        GeneroUI(6, "Urbano",     0xFFEF4444),
        GeneroUI(7, "Rock",       0xFF9333EA),
        GeneroUI(8, "Jazz",       0xFF14B8A6)
    )

    val nuevosLanzamientos = listOf(
        AlbumDescubreUI(1, "Midnights",         "Taylor Swift", R.drawable.album_midnights),
        AlbumDescubreUI(2, "Renaissance",        "Beyoncé",      R.drawable.album_renaissance),
        AlbumDescubreUI(3, "Un Verano Sin Ti",   "Bad Bunny",    R.drawable.album5),
        AlbumDescubreUI(4, "Harry's House",      "Harry Styles", R.drawable.album_harrys_house),
        AlbumDescubreUI(5, "Dawn FM",            "The Weeknd",   R.drawable.album_dawn_fm)
    )
}