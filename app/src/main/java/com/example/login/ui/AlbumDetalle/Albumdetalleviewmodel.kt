package com.example.login.ui.AlbumDetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreAlbumRepository
import com.example.login.data.repository.FirestoreReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class AlbumDetalleViewModel @Inject constructor(
    private val firestoreAlbumRepository: FirestoreAlbumRepository,
    private val firestoreReviewRepository: FirestoreReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetalleUIState())
    val uiState: StateFlow<AlbumDetalleUIState> = _uiState.asStateFlow()

    // The Firestore string ID for this album (needed to load reviews)
    private var firestoreAlbumId: String = ""

    fun cargarAlbum(albumId: Int) {
        // albumId here is hashCode(firestoreDocId). We need the original string.
        // We store it when we first load.
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // Get all albums to find the one matching this hashCode
            val rawResult = firestoreAlbumRepository.getAllAlbumsRaw()
            if (rawResult.isSuccess) {
                val albumsMap = rawResult.getOrDefault(emptyMap())
                val entry = albumsMap.entries.find { it.key.hashCode() == albumId }

                if (entry != null) {
                    firestoreAlbumId = entry.key
                    val dto = entry.value
                    val album = AlbumDetalleUI(
                        id                   = albumId,
                        nombre               = dto.title,
                        artista              = dto.artist,
                        año                  = dto.releaseYear.toString(),
                        genero               = dto.genre,
                        descripcion          = dto.description,
                        imagenUrl            = dto.coverImage,
                        duracionTotal        = "—",
                        calificacionPromedio = 0f,
                        totalResenas         = 0,
                        canciones            = emptyList()
                    )
                    _uiState.update { it.copy(album = album, isLoading = false) }
                    cargarResenas(entry.key)
                } else {
                    // Fallback: try treating albumId as a direct int ID for legacy navigation
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Álbum no encontrado") }
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = rawResult.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun cargarResenas(firestoreId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(resenasLoading = true) }
            val result = firestoreReviewRepository.getReviewsByAlbum(firestoreId)
            if (result.isSuccess) {
                val resenas = result.getOrDefault(emptyList())
                _uiState.update { state ->
                    val albumActualizado = if (state.album != null && resenas.isNotEmpty()) {
                        val promedio = calcularPromedio(resenas.map { it.calificacion })
                        state.album.copy(
                            calificacionPromedio = promedio,
                            totalResenas         = resenas.size
                        )
                    } else state.album

                    state.copy(
                        resenas        = resenas,
                        resenasLoading = false,
                        album          = albumActualizado
                    )
                }
            } else {
                _uiState.update {
                    it.copy(resenasLoading = false, resenasError = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun calcularPromedio(calificaciones: List<Float>): Float {
        if (calificaciones.isEmpty()) return 0f
        return (round(calificaciones.average() * 10) / 10).toFloat()
    }

    fun toggleFavorito() {
        _uiState.update { it.copy(esFavorito = !it.esFavorito) }
    }
}