// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/AlbumDetalle/AlbumDetalleViewModel.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
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

@HiltViewModel
class AlbumDetalleViewModel @Inject constructor(
    private val firestoreAlbumRepository: FirestoreAlbumRepository,
    private val firestoreReviewRepository: FirestoreReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetalleUIState())
    val uiState: StateFlow<AlbumDetalleUIState> = _uiState.asStateFlow()

    // Guarda el firestoreId del álbum actual para los reviews
    private var currentFirestoreId: String = ""

    fun cargarAlbum(firestoreId: String) {
        currentFirestoreId = firestoreId
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // Carga álbum
            val albumResult = firestoreAlbumRepository.getAlbumById(firestoreId)
            if (albumResult.isSuccess) {
                _uiState.update { it.copy(album = albumResult.getOrNull(), isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = albumResult.exceptionOrNull()?.message
                    )
                }
            }

            // Carga reviews
            _uiState.update { it.copy(resenasLoading = true) }
            val reviewsResult = firestoreReviewRepository.getReviewsByAlbum(firestoreId)
            if (reviewsResult.isSuccess) {
                val resenas = reviewsResult.getOrDefault(emptyList())
                _uiState.update { state ->
                    state.copy(
                        resenas        = resenas,
                        resenasLoading = false,
                        album = state.album?.let { alb ->
                            if (resenas.isNotEmpty()) {
                                val promedio = resenas.map { it.calificacion }.average().toFloat()
                                alb.copy(
                                    calificacionPromedio = promedio,
                                    totalResenas         = resenas.size
                                )
                            } else alb
                        }
                    )
                }
            } else {
                _uiState.update { it.copy(resenasLoading = false) }
            }
        }
    }

    // Sobrecarga para compatibilidad con código existente que pasa Int
    fun cargarAlbum(albumId: Int) {
        // Si no tenemos el firestoreId real, usamos el hashCode como fallback
        cargarAlbum(albumId.toString())
    }

    fun getCurrentFirestoreId(): String = currentFirestoreId

    fun toggleFavorito() {
        _uiState.update { it.copy(esFavorito = !it.esFavorito) }
    }
}
