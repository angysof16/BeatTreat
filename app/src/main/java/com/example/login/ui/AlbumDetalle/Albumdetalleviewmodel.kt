package com.example.login.ui.AlbumDetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AlbumRepository
import com.example.login.data.repository.ReviewRepository
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
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetalleUIState())
    val uiState: StateFlow<AlbumDetalleUIState> = _uiState.asStateFlow()

    fun cargarAlbum(albumId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Carga del álbum
        viewModelScope.launch {
            val result = albumRepository.getAlbumById(albumId)
            if (result.isSuccess) {
                _uiState.update { state ->
                    val album = result.getOrNull()
                    // Si las reseñas ya cargaron antes que el álbum (race condition), aplicar promedio
                    val albumConPromedio = if (album != null && state.resenas.isNotEmpty()) {
                        val promedio = calcularPromedio(state.resenas.map { it.calificacion })
                        album.copy(calificacionPromedio = promedio, totalResenas = state.resenas.size)
                    } else album
                    state.copy(album = albumConPromedio, isLoading = false)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message)
                }
            }
        }

        // Carga de reseñas en paralelo
        viewModelScope.launch {
            _uiState.update { it.copy(resenasLoading = true) }
            val result = reviewRepository.getReviewsByAlbum(albumId)
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