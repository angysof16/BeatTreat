package com.example.beattreat.ui.Descubre

object DescubreData {

    val categorias = listOf(
        CategoriaUI(1, "Nuevos\nlanzamientos", 0xFF6366F1, "HTTPS://PLACEHOLDER.COM/CATEGORIAS/NUEVOS_LANZAMIENTOS.JPG"),
        CategoriaUI(2, "Géneros y\nEstadísticas", 0xFF8B5CF6, "HTTPS://PLACEHOLDER.COM/CATEGORIAS/GENEROS.JPG"),
        CategoriaUI(3, "Rankings", 0xFF10B981, "HTTPS://PLACEHOLDER.COM/CATEGORIAS/RANKINGS.JPG"),
        CategoriaUI(4, "Podcasts", 0xFFEC4899, "HTTPS://PLACEHOLDER.COM/CATEGORIAS/PODCASTS.JPG")
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
        AlbumDescubreUI(1, "Midnights",       "Taylor Swift", "HTTPS://PLACEHOLDER.COM/ALBUMS/TAYLOR_SWIFT_MIDNIGHTS.JPG"),
        AlbumDescubreUI(2, "Renaissance",     "Beyoncé",      "HTTPS://PLACEHOLDER.COM/ALBUMS/BEYONCE_RENAISSANCE.JPG"),
        AlbumDescubreUI(3, "Un Verano Sin Ti","Bad Bunny",    "HTTPS://PLACEHOLDER.COM/ALBUMS/BAD_BUNNY_UVST.JPG"),
        AlbumDescubreUI(4, "Harry's House",   "Harry Styles", "HTTPS://PLACEHOLDER.COM/ALBUMS/HARRY_STYLES_HARRYS_HOUSE.JPG"),
        AlbumDescubreUI(5, "Dawn FM",         "The Weeknd",   "HTTPS://PLACEHOLDER.COM/ALBUMS/WEEKND_DAWN_FM.JPG")
    )
}