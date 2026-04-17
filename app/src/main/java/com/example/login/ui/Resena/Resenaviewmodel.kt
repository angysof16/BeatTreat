// ──────────────────────────────────────────────────────────────────────────────
// FILE: ui/Resena/ResenaViewModel.kt  (REEMPLAZA el existente)
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.ui.Resena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.FirestoreReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResenaViewModel @Inject constructor(
    private val firestoreReviewRepository: FirestoreReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResenaUIState())
    val uiState: StateFlow<ResenaUIState> = _uiState.asStateFlow()

    // Guarda el firestoreId actual para pasarlo a EscribirResena
    private var currentFirestoreAlbumId: String = ""

    fun cargarResenas(firestoreAlbumId: String) {
        currentFirestoreAlbumId = firestoreAlbumId
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = firestoreReviewRepository.getReviewsByAlbum(firestoreAlbumId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(resenas = result.getOrDefault(emptyList()), isLoading = false)
                }
            } else {
                // Fallback a datos locales
                val resenasLocales = ResenaData.porAlbum(firestoreAlbumId.hashCode())
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

    // Compatibilidad con código que pasa Int
    fun cargarResenas(albumId: Int) {
        cargarResenas(albumId.toString())
    }

    fun getCurrentFirestoreAlbumId(): String = currentFirestoreAlbumId

    fun toggleLikeResena(resenaId: Int) {
        _uiState.update { state ->
            val likeadas = state.resenasLikeadas
            val nuevasLikeadas = if (resenaId in likeadas) likeadas - resenaId
                                 else likeadas + resenaId
            state.copy(resenasLikeadas = nuevasLikeadas)
        }
    }
}
