package com.example.beattreat.ui.EscribirResena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.AlbumRepository
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.data.repository.FirestoreReviewRepository
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
    private val firestoreAlbumRepository: FirestoreAlbumRepository,
    private val albumRepository: AlbumRepository  // kept for album list selector (REST)
) : ViewModel() {

    private val _uiState = MutableStateFlow(EscribirResenaUIState())
    val uiState: StateFlow<EscribirResenaUIState> = _uiState.asStateFlow()

    // Maps "Title — Artist" label -> firestoreId
    private val labelToFirestoreId = mutableMapOf<String, String>()

    // Pending album hash id (set before albums load)
    private var albumIdPendiente: Int = 0

    init {
        cargarAlbumesFirestore()
    }

    private fun cargarAlbumesFirestore() {
        viewModelScope.launch {
            _uiState.update { it.copy(albumesCargando = true) }
            val result = firestoreAlbumRepository.getAllAlbumsRaw()
            result.onSuccess { albumsMap ->
                labelToFirestoreId.clear()
                albumsMap.forEach { (firestoreId, dto) ->
                    labelToFirestoreId["${dto.title} — ${dto.artist}"] = firestoreId
                }

                // Also build AlbumDto list for the selector UI (reuse existing UI structure)
                val dtoList = albumsMap.entries.map { (firestoreId, dto) ->
                    com.example.beattreat.data.dto.AlbumDto(
                        id          = firestoreId.hashCode(),
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
                    val idToUse = if (albumIdPendiente != 0) albumIdPendiente else state.albumId
                    val entry = if (idToUse != 0) albumsMap.entries.find { it.key.hashCode() == idToUse } else null
                    val etiqueta = entry?.let { "${it.value.title} — ${it.value.artist}" } ?: state.albumSeleccionado
                    val firestoreId = entry?.key ?: state.firestoreAlbumId

                    state.copy(
                        albumesBackend    = dtoList,
                        albumesCargando   = false,
                        albumId           = if (albumIdPendiente != 0) albumIdPendiente else state.albumId,
                        firestoreAlbumId  = firestoreId,
                        albumSeleccionado = etiqueta,
                        albumFijado       = if (albumIdPendiente != 0) true else state.albumFijado
                    )
                }
                albumIdPendiente = 0
            }.onFailure {
                _uiState.update { it.copy(albumesCargando = false) }
            }
        }
    }

    fun preSeleccionarAlbum(albumId: Int) {
        if (albumId == 0) return

        val albumsMap = labelToFirestoreId
        if (albumsMap.isNotEmpty()) {
            // Find the entry whose hashCode matches
            val entry = labelToFirestoreId.entries.find { it.key.hashCode() == albumId }
                ?: labelToFirestoreId.entries.find { it.value.hashCode() == albumId }

            // Alternatively search by albumId in dtos
            val dto = _uiState.value.albumesBackend.find { it.id == albumId }
            val etiqueta = dto?.let { "${it.title} — ${it.artist}" } ?: ""
            val firestoreId = if (etiqueta.isNotBlank()) labelToFirestoreId[etiqueta] ?: "" else ""

            _uiState.update {
                it.copy(
                    albumId           = albumId,
                    firestoreAlbumId  = firestoreId,
                    albumSeleccionado = etiqueta,
                    albumFijado       = true
                )
            }
        } else {
            albumIdPendiente = albumId
            _uiState.update { it.copy(albumId = albumId, albumFijado = true) }
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

        val dto = state.albumesBackend.find { "${it.title} — ${it.artist}" == albumLabel }
        val firestoreId = labelToFirestoreId[albumLabel] ?: ""

        if (dto != null) {
            _uiState.update {
                it.copy(
                    albumSeleccionado = albumLabel,
                    albumId           = dto.id,
                    firestoreAlbumId  = firestoreId
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
        if (state.isLoading || state.publicadoExitoso) return

        if (state.textoResena.isBlank() || state.calificacion == 0f || state.firestoreAlbumId.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Selecciona un álbum válido, calificación y escribe tu reseña.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = firestoreReviewRepository.createReview(
                albumId = state.firestoreAlbumId,
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
        _uiState.update {
            it.copy(
                publicadoExitoso  = false,
                textoResena       = "",
                calificacion      = 0f,
                albumSeleccionado = if (it.albumFijado) it.albumSeleccionado else "",
                albumId           = if (it.albumFijado) it.albumId else 0,
                firestoreAlbumId  = if (it.albumFijado) it.firestoreAlbumId else "",
                errorMessage      = null
            )
        }
    }
}