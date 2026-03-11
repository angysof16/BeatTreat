package com.example.login.Home

import androidx.lifecycle.ViewModel
import com.example.login.Home.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        cargarHome()
    }

    private fun cargarHome() {
        _uiState.update {
            it.copy(
                artistas  = HomeData.artistas,
                isLoading = false
            )
        }
    }

    fun onBannerChange(index: Int) {
        _uiState.update { it.copy(bannerActual = index) }
    }

    fun bannerAnterior() {
        val actual = _uiState.value.bannerActual
        if (actual > 0) _uiState.update { it.copy(bannerActual = actual - 1) }
    }

    fun bannerSiguiente() {
        _uiState.update { it.copy(bannerActual = it.bannerActual + 1) }
    }
}