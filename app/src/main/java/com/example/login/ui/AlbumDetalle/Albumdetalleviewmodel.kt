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

@HiltViewModel
class AlbumDetalleViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetalleUIState())
    val uiState: StateFlow<AlbumDetalleUIState> = _uiState.asStateFlow()

    fun cargarAlbum(albumId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = albumRepository.getAlbumById(albumId)
            if (result.isSuccess) {
                _uiState.update { it.copy(album = result.getOrNull(), isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message)
                }
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(resenasLoading = true) }
            val result = reviewRepository.getReviewsByAlbum(albumId)
            if (result.isSuccess) {
                val resenas = result.getOrDefault(emptyList())
                _uiState.update { state ->
                    state.copy(
                        resenas        = resenas,
                        resenasLoading = false,
                        album = state.album?.let { alb ->
                            if (resenas.isNotEmpty()) {
                                val promedio = resenas.map { it.calificacion }.average().toFloat()
                                alb.copy(calificacionPromedio = promedio, totalResenas = resenas.size)
                            } else alb
                        }
                    )
                }
            } else {
                _uiState.update {
                    it.copy(resenasLoading = false, resenasError = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun toggleFavorito() {
        _uiState.update { it.copy(esFavorito = !it.esFavorito) }
    }
}