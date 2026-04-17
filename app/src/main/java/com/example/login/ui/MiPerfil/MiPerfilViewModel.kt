package com.example.login.ui.MiPerfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.MiPerfilRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MiPerfilViewModel @Inject constructor(
    private val repository: MiPerfilRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MiPerfilUIState())
    val uiState: StateFlow<MiPerfilUIState> = _uiState.asStateFlow()

    init {
        cargarMisResenas()
    }

    // ── Carga ─────────────────────────────────────────────────────────────────

    fun cargarMisResenas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getMisResenas()
                .onSuccess { lista ->
                    _uiState.update { it.copy(misResenas = lista, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    // ── Formulario crear / editar ─────────────────────────────────────────────

    fun abrirFormularioCrear(albumId: Int = 0) {
        _uiState.update {
            it.copy(
                mostrarFormulario = true,
                resenaEnEdicion   = null,
                formularioAlbumId = albumId,
                formularioRating  = 0f,
                formularioContent = ""
            )
        }
    }

    fun abrirFormularioEditar(resena: MiResenaUI) {
        _uiState.update {
            it.copy(
                mostrarFormulario = true,
                resenaEnEdicion   = resena,
                formularioAlbumId = resena.albumId,
                formularioRating  = resena.rating,
                formularioContent = resena.content
            )
        }
    }

    fun cerrarFormulario() {
        _uiState.update { it.copy(mostrarFormulario = false, resenaEnEdicion = null) }
    }

    fun onAlbumIdChange(albumId: Int) {
        _uiState.update { it.copy(formularioAlbumId = albumId) }
    }

    fun onRatingChange(rating: Float) {
        _uiState.update { it.copy(formularioRating = rating) }
    }

    fun onContentChange(content: String) {
        _uiState.update { it.copy(formularioContent = content) }
    }

    fun guardarResena() {
        val state = _uiState.value
        val content = state.formularioContent.trim()
        if (content.isBlank() || state.formularioRating == 0f) return

        // Guard: no permitir guardar si ya está cargando
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = if (state.resenaEnEdicion == null) {
                // ── CREAR ──
                repository.crearResena(
                    albumId = state.formularioAlbumId,
                    rating  = state.formularioRating,
                    content = content
                )
            } else {
                // ── EDITAR ──
                repository.editarResena(
                    reviewId = state.resenaEnEdicion.id,
                    rating   = state.formularioRating,
                    content  = content
                )
            }

            result
                .onSuccess {
                    val mensajeExito = if (state.resenaEnEdicion == null)
                        "¡Reseña creada!" else "¡Reseña actualizada!"

                    _uiState.update {
                        it.copy(
                            mostrarFormulario = false,
                            resenaEnEdicion   = null,
                            isLoading         = false,
                            successMessage    = mensajeExito
                        )
                    }
                    // Refresca la lista desde el servidor para ver cambios reales
                    cargarMisResenas()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────

    fun pedirConfirmarEliminar(resena: MiResenaUI) {
        _uiState.update { it.copy(mostrarConfirmarEliminar = true, resenaAEliminar = resena) }
    }

    fun cancelarEliminar() {
        _uiState.update { it.copy(mostrarConfirmarEliminar = false, resenaAEliminar = null) }
    }

    fun confirmarEliminar() {
        val resena = _uiState.value.resenaAEliminar ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mostrarConfirmarEliminar = false) }
            repository.eliminarResena(resena.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading       = false,
                            resenaAEliminar = null,
                            successMessage  = "Reseña eliminada"
                        )
                    }
                    cargarMisResenas()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    // ── Mensajes ──────────────────────────────────────────────────────────────

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}