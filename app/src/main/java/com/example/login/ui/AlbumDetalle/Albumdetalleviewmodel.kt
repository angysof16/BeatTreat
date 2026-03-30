package com.example.login.ui.AlbumDetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetalleViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetalleUIState())
    val uiState: StateFlow<AlbumDetalleUIState> = _uiState.asStateFlow()

    fun cargarAlbum(albumId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = albumRepository.getAlbumById(albumId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        album     = result.getOrNull(),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun toggleFavorito() {
        _uiState.update { it.copy(esFavorito = !it.esFavorito) }
    }
}