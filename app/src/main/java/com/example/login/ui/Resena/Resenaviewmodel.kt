package com.example.login.ui.Resena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResenaViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResenaUIState())
    val uiState: StateFlow<ResenaUIState> = _uiState.asStateFlow()

    fun cargarResenas(albumId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, albumId = albumId) }

        viewModelScope.launch {
            val result = reviewRepository.getReviewsByAlbum(albumId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(resenas = result.getOrDefault(emptyList()), isLoading = false)
                }
            } else {
                val resenasLocales = ResenaData.porAlbum(albumId)
                _uiState.update {
                    it.copy(
                        resenas      = resenasLocales,
                        isLoading    = false,
                        errorMessage = if (resenasLocales.isEmpty())
                            result.exceptionOrNull()?.message else null
                    )
                }
            }
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