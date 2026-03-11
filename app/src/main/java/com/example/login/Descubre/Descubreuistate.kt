package com.example.login.Descubre

data class DescubreUIState(
    val categorias: List<CategoriaUI> = emptyList(),
    val generos: List<GeneroUI> = emptyList(),
    val nuevosLanzamientos: List<AlbumDescubreUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)