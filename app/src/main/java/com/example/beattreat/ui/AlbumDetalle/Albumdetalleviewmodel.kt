package com.example.beattreat.ui.AlbumDetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.data.repository.FirestoreReviewRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class AlbumDetalleViewModel @Inject constructor(
    private val firestoreAlbumRepository: FirestoreAlbumRepository,
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetalleUIState())
    val uiState: StateFlow<AlbumDetalleUIState> = _uiState.asStateFlow()

    private var firestoreAlbumId: String = ""

    fun cargarAlbum(albumId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val rawResult = firestoreAlbumRepository.getAllAlbumsRaw()
            if (rawResult.isSuccess) {
                val albumsMap = rawResult.getOrDefault(emptyMap())
                val entry = albumsMap.entries.find { it.key.hashCode() == albumId }
                if (entry != null) {
                    firestoreAlbumId = entry.key
                    val dto = entry.value
                    val album = AlbumDetalleUI(
                        id                   = albumId,
                        nombre               = dto.title,
                        artista              = dto.artist,
                        año                  = dto.releaseYear.toString(),
                        genero               = dto.genre,
                        descripcion          = dto.description,
                        imagenUrl            = dto.coverImage,
                        duracionTotal        = "—",
                        calificacionPromedio = 0f,
                        totalResenas         = 0,
                        canciones            = emptyList()
                    )
                    _uiState.update { it.copy(album = album, isLoading = false, firestoreAlbumId = firestoreAlbumId) }
                    cargarResenas(entry.key)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Álbum no encontrado") }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = rawResult.exceptionOrNull()?.message) }
            }
        }
    }

    private fun cargarResenas(firestoreId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(resenasLoading = true) }
            val result = firestoreReviewRepository.getReviewsByAlbum(firestoreId)
            if (result.isSuccess) {
                val resenas = result.getOrDefault(emptyList())
                _uiState.update { state ->
                    val albumActualizado = state.album?.takeIf { resenas.isNotEmpty() }?.copy(
                        calificacionPromedio = calcularPromedio(resenas.map { it.calificacion }),
                        totalResenas         = resenas.size
                    ) ?: state.album
                    state.copy(resenas = resenas, resenasLoading = false, album = albumActualizado)
                }
            } else {
                _uiState.update { it.copy(resenasLoading = false, resenasError = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun recargarResenas() {
        if (firestoreAlbumId.isNotBlank()) cargarResenas(firestoreAlbumId)
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────

    fun eliminarResena(firestoreDocId: String) {
        viewModelScope.launch {
            firestoreReviewRepository.deleteReview(firestoreDocId)
            recargarResenas()
        }
    }

    // ── Editar ────────────────────────────────────────────────────────────────

    fun abrirEditar(resena: com.example.beattreat.ui.Resena.ResenaDetalladaUI) {
        _uiState.update {
            it.copy(
                resenaEditando   = resena,
                editRating       = resena.calificacion,
                editContent      = resena.texto,
                mostrarDialogoEditar = true
            )
        }
    }

    fun cerrarEditar() {
        _uiState.update { it.copy(mostrarDialogoEditar = false, resenaEditando = null) }
    }

    fun onEditRatingChange(v: Float) { _uiState.update { it.copy(editRating = v) } }
    fun onEditContentChange(v: String) { _uiState.update { it.copy(editContent = v) } }

    fun guardarEdicion() {
        val state  = _uiState.value
        val resena = state.resenaEditando ?: return
        if (state.editContent.isBlank() || state.editRating == 0f) return

        viewModelScope.launch {
            _uiState.update { it.copy(editGuardando = true) }
            firestoreReviewRepository.updateReview(resena.firestoreDocId, state.editRating, state.editContent.trim())
                .onSuccess {
                    _uiState.update { it.copy(mostrarDialogoEditar = false, resenaEditando = null, editGuardando = false) }
                    recargarResenas()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(editGuardando = false, resenasError = e.message) }
                }
        }
    }

    // ── Util ──────────────────────────────────────────────────────────────────

    fun toggleFavorito() { _uiState.update { it.copy(esFavorito = !it.esFavorito) } }

    fun getCurrentUserId(): String = firebaseAuth.currentUser?.uid ?: ""

    private fun calcularPromedio(calificaciones: List<Float>): Float {
        if (calificaciones.isEmpty()) return 0f
        return (round(calificaciones.average() * 10) / 10).toFloat()
    }
}