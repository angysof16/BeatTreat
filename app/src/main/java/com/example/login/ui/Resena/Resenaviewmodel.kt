package com.example.login.ui.Resena

import androidx.lifecycle.ViewModel
import com.example.login.ui.AlbumDetalle.AlbumDetalleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ResenaViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(ResenaUIState())
    val uiState: StateFlow<ResenaUIState> = _uiState.asStateFlow()

    // Carga solo las reseñas del álbum recibido
    fun cargarResenas(albumId: Int) {
        val album = AlbumDetalleData.findById(albumId)
        _uiState.update {
            it.copy(
                albumId      = albumId,
                albumNombre  = album?.nombre ?: "",
                resenas      = ResenaData.porAlbum(albumId),
                isLoading    = false
            )
        }
    }

    fun toggleLikeResena(resenaId: Int) {
        _uiState.update { state ->
            val likeadas = state.resenasLikeadas
            val nuevasLikeadas = if (resenaId in likeadas) likeadas - resenaId
            else likeadas + resenaId
            state.copy(resenasLikeadas = nuevasLikeadas)
        }
    }
}