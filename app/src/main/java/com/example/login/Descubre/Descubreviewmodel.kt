package com.example.login.Descubre

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DescubreViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(DescubreUIState())
    val uiState: StateFlow<DescubreUIState> = _uiState.asStateFlow()

    init {
        cargarContenido()
    }

    private fun cargarContenido() {
        _uiState.update {
            it.copy(
                categorias         = DescubreData.categorias,
                generos            = DescubreData.generos,
                nuevosLanzamientos = DescubreData.nuevosLanzamientos,
                isLoading          = false
            )
        }
    }
}