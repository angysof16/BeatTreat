package com.example.login.ui.EscribirResena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AlbumRepository
import com.example.login.data.repository.MiPerfilRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EscribirResenaViewModel @Inject constructor(
    private val repository: MiPerfilRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EscribirResenaUIState())
    val uiState: StateFlow<EscribirResenaUIState> = _uiState.asStateFlow()

    // Guardamos el albumId pendiente en caso de que llegue antes de que carguen los álbumes
    private var albumIdPendiente: Int = 0

    init {
        cargarAlbumesDelBackend()
    }

    private fun cargarAlbumesDelBackend() {
        viewModelScope.launch {
            _uiState.update { it.copy(albumesCargando = true) }
            try {
                val result = albumRepository.getAllAlbumsDto()
                result.onSuccess { dtos ->
                    _uiState.update { state ->
                        // Si había un albumId pendiente de pre-selección, aplicarlo ahora
                        val idToUse = if (albumIdPendiente != 0) albumIdPendiente else state.albumId
                        val etiqueta = if (idToUse != 0) {
                            dtos.find { it.id == idToUse }
                                ?.let { "${it.title} — ${it.artist}" }
                                ?: state.albumSeleccionado
                        } else state.albumSeleccionado

                        state.copy(
                            albumesBackend    = dtos,
                            albumesCargando   = false,
                            albumId           = if (albumIdPendiente != 0) albumIdPendiente else state.albumId,
                            albumSeleccionado = etiqueta,
                            albumFijado       = if (albumIdPendiente != 0) true else state.albumFijado
                        )
                    }
                    albumIdPendiente = 0
                }.onFailure {
                    _uiState.update { it.copy(albumesCargando = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(albumesCargando = false) }
            }
        }
    }

    fun preSeleccionarAlbum(albumId: Int) {
        if (albumId == 0) return

        val dtos = _uiState.value.albumesBackend
        if (dtos.isNotEmpty()) {
            // Los álbumes ya cargaron, aplicar inmediatamente
            val etiqueta = dtos.find { it.id == albumId }
                ?.let { "${it.title} — ${it.artist}" }
                ?: ""
            _uiState.update {
                it.copy(
                    albumId           = albumId,
                    albumSeleccionado = etiqueta,
                    albumFijado       = true
                )
            }
        } else {
            // Los álbumes aún no cargaron: guardar como pendiente
            albumIdPendiente = albumId
            _uiState.update {
                it.copy(
                    albumId     = albumId,
                    albumFijado = true
                )
            }
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

        val dtoEncontrado = state.albumesBackend.find { dto ->
            "${dto.title} — ${dto.artist}" == albumLabel
        }
        if (dtoEncontrado != null) {
            _uiState.update { it.copy(albumSeleccionado = albumLabel, albumId = dtoEncontrado.id) }
            return
        }
        val dtoParcial = state.albumesBackend.find { dto ->
            albumLabel.contains(dto.title, ignoreCase = true) &&
                    albumLabel.contains(dto.artist, ignoreCase = true)
        }
        if (dtoParcial != null) {
            _uiState.update { it.copy(albumSeleccionado = albumLabel, albumId = dtoParcial.id) }
            return
        }
        _uiState.update {
            it.copy(
                albumSeleccionado = albumLabel,
                albumId           = 0,
                errorMessage      = "No se pudo identificar el álbum. Verifica la conexión."
            )
        }
    }

    fun publicarResena() {
        val state = _uiState.value

        if (state.isLoading || state.publicadoExitoso) return

        if (state.textoResena.isBlank() || state.calificacion == 0f || state.albumId == 0) {
            _uiState.update {
                it.copy(errorMessage = "Selecciona un álbum válido, calificación y escribe tu reseña.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.crearResena(
                albumId = state.albumId,
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
                errorMessage      = null
            )
        }
    }
}