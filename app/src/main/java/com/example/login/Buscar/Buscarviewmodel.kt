package com.example.login.Buscar

import androidx.lifecycle.ViewModel
import com.example.login.Home.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BuscarViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BuscarUIState())
    val uiState: StateFlow<BuscarUIState> = _uiState.asStateFlow()

    // Fuentes de datos locales aplanadas para la búsqueda
    private val todosLosAlbumes: List<AlbumBuscarUI> by lazy {
        HomeData.artistas.flatMap { artista ->
            artista.albumes.map { album ->
                AlbumBuscarUI(id = album.id, nombre = album.nombre, artista = artista.nombre)
            }
        }
    }

    private val todosLosArtistas: List<ArtistaBuscarUI> by lazy {
        HomeData.artistas.map { ArtistaBuscarUI(id = it.id, nombre = it.nombre) }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        buscar(query)
    }

    private fun buscar(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(resultadosAlbumes = emptyList(), resultadosArtistas = emptyList()) }
            return
        }
        val q = query.trim().lowercase()
        _uiState.update {
            it.copy(
                resultadosAlbumes  = todosLosAlbumes.filter { a ->
                    a.nombre.lowercase().contains(q) || a.artista.lowercase().contains(q)
                },
                resultadosArtistas = todosLosArtistas.filter { a ->
                    a.nombre.lowercase().contains(q)
                }
            )
        }
    }

    fun limpiar() {
        _uiState.update { BuscarUIState() }
    }
}