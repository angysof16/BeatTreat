package com.example.login.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AlbumRepository
import com.example.login.ui.Perfil.PerfilData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        cargarHome()
    }

    fun cargarHome() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = albumRepository.getAllAlbums()

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        artistas      = result.getOrDefault(emptyList()),
                        fotoPerfilUrl = PerfilData.perfilActual.fotoPerfilUrl,
                        isLoading     = false
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

    fun refrescarFotoPerfil() {
        _uiState.update { it.copy(fotoPerfilUrl = PerfilData.perfilActual.fotoPerfilUrl) }
    }

    fun onBannerChange(index: Int) {
        val bounded = index.coerceIn(0, HomeData.banners.lastIndex)
        _uiState.update { it.copy(bannerActual = bounded) }
    }
}