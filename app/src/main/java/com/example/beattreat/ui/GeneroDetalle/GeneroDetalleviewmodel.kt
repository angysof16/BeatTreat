package com.example.beattreat.ui.GeneroDetalle

import androidx.lifecycle.ViewModel
import com.example.beattreat.ui.AlbumDetalle.AlbumDetalleData
import com.example.beattreat.ui.Descubre.DescubreData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GeneroDetalleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(GeneroDetalleUIState())
    val uiState: StateFlow<GeneroDetalleUIState> = _uiState.asStateFlow()

    fun cargarGenero(generoId: Int) {
        val genero = DescubreData.generos.find { it.id == generoId } ?: return
        val albumes = AlbumDetalleData.porGenero(genero.nombre)
        _uiState.update {
            it.copy(
                nombre     = genero.nombre,
                colorFondo = genero.colorChip,
                albumes    = albumes.map { a -> AlbumGeneroUI(a.id, a.nombre, a.artista) },
                isLoading  = false
            )
        }
    }

    fun cargarPorCategoria(categoriaId: Int) {
        val categoria = DescubreData.categorias.find { it.id == categoriaId } ?: return
        val palabraClave = when (categoriaId) {
            3    -> "Rock"
            4    -> "Pop"
            else -> ""
        }
        val albumes = if (palabraClave.isEmpty()) {
            AlbumDetalleData.todos().take(6)
        } else {
            AlbumDetalleData.porGenero(palabraClave)
        }
        _uiState.update {
            it.copy(
                nombre     = categoria.nombre.replace("\n", " "),
                colorFondo = categoria.colorFondo,
                albumes    = albumes.map { a -> AlbumGeneroUI(a.id, a.nombre, a.artista) },
                isLoading  = false
            )
        }
    }
}