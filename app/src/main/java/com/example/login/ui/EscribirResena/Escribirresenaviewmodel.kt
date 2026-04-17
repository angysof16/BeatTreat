// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/EscribirResena/EscribirResenaViewModel.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.EscribirResena

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
class EscribirResenaViewModel @Inject constructor(
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val firestoreAlbumRepository: FirestoreAlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EscribirResenaUIState())
    val uiState: StateFlow<EscribirResenaUIState> = _uiState.asStateFlow()

    // Map de título mostrado → firestoreId real
    private val albumLabelToFirestoreId = mutableMapOf<String, String>()

    init {
        cargarAlbumesDeFirestore()
    }

    private fun cargarAlbumesDeFirestore() {
        viewModelScope.launch {
            _uiState.update { it.copy(albumesCargando = true) }
            firestoreAlbumRepository.getAllAlbumsRaw()
                .onSuccess { albumsMap ->
                    albumLabelToFirestoreId.clear()
                    val dtosList = albumsMap.entries.map { (id, dto) ->
                        albumLabelToFirestoreId["${dto.title} — ${dto.artist}"] = id
                        // Convertimos a AlbumDto para reutilizar el selector existente
                        com.example.login.data.dto.AlbumDto(
                            id          = id.hashCode(),
                            title       = dto.title,
                            artist      = dto.artist,
                            genre       = dto.genre,
                            releaseYear = dto.releaseYear,
                            coverImage  = dto.coverImage,
                            description = dto.description,
                            createdAt   = null,
                            updatedAt   = null
                        )
                    }
                    _uiState.update { state ->
                        val etiqueta = if (state.albumFijado && state.firestoreAlbumId.isNotBlank()) {
                            dtosList.find { it.id == state.albumId }
                                ?.let { "${it.title} — ${it.artist}" }
                                ?: state.albumSeleccionado
                        } else state.albumSeleccionado
                        state.copy(
                            albumesBackend  = dtosList,
                            albumesCargando = false,
                            albumSeleccionado = etiqueta
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(albumesCargando = false) }
                }
        }
    }

    fun preSeleccionarAlbum(firestoreId: String) {
        if (firestoreId.isBlank()) return
        val etiqueta = albumLabelToFirestoreId.entries
            .find { it.value == firestoreId }?.key ?: ""
        _uiState.update {
            it.copy(
                firestoreAlbumId  = firestoreId,
                albumId           = firestoreId.hashCode(),
                albumSeleccionado = etiqueta,
                albumFijado       = true
            )
        }
    }

    // Mantiene compatibilidad con el flujo que pasa Int
    fun preSeleccionarAlbum(albumId: Int) {
        if (albumId == 0) return
        _uiState.update {
            it.copy(albumId = albumId, albumFijado = true)
        }
    }

    fun onTextoChange(texto: String) {
        if (texto.length <= 500) _uiState.update { it.copy(textoResena = texto) }
    }

    fun onCalificacionChange(calificacion: Float) {
        _uiState.update { it.copy(calificacion = calificacion) }
    }

    fun onAlbumSeleccionado(albumLabel: String) {
        val state = _uiState.value
        if (state.albumFijado) return
        val firestoreId = albumLabelToFirestoreId[albumLabel]
        if (firestoreId != null) {
            _uiState.update {
                it.copy(
                    albumSeleccionado = albumLabel,
                    albumId           = firestoreId.hashCode(),
                    firestoreAlbumId  = firestoreId,
                    errorMessage      = null
                )
            }
        } else {
            _uiState.update {
                it.copy(albumSeleccionado = albumLabel, errorMessage = "No se pudo identificar el álbum.")
            }
        }
    }

    fun publicarResena() {
        val state = _uiState.value
        val firestoreId = state.firestoreAlbumId

        if (state.textoResena.isBlank() || state.calificacion == 0f || firestoreId.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Selecciona un álbum, calificación y escribe tu reseña.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = firestoreReviewRepository.createReview(
                albumId = firestoreId,
                rating  = state.calificacion,
                content = state.textoResena
            )
            if (result.isSuccess) {
                _uiState.update { it.copy(publicadoExitoso = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun resetPublicado() {
        _uiState.update { it.copy(publicadoExitoso = false) }
    }
}
