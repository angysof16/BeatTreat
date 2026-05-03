package com.example.beattreat.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.ui.Perfil.PerfilData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreAlbumRepository: FirestoreAlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    // Maps hashCode(firestoreId) -> firestoreId string, for navigation
    private val _firestoreAlbumIds = mutableMapOf<Int, String>()
    val firestoreAlbumIds: Map<Int, String> get() = _firestoreAlbumIds

    init {
        cargarHome()
    }

    fun cargarHome() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // Get raw map to build correct AlbumHomeUI with real images and correct IDs
            val rawResult = firestoreAlbumRepository.getAllAlbumsRaw()

            if (rawResult.isSuccess) {
                val albumsMap = rawResult.getOrDefault(emptyMap())

                // Clear and rebuild ID map
                _firestoreAlbumIds.clear()
                albumsMap.forEach { (firestoreId, _) ->
                    _firestoreAlbumIds[firestoreId.hashCode()] = firestoreId
                }

                // Group by artist, using the real coverImage from Firestore
                val artistasAgrupados = albumsMap.entries
                    .groupBy { it.value.artist }
                    .entries
                    .mapIndexed { index, (artista, entries) ->
                        ArtistaHomeUI(
                            id     = index + 1,
                            nombre = artista,
                            albumes = entries.map { (firestoreId, dto) ->
                                AlbumHomeUI(
                                    id        = firestoreId.hashCode(),
                                    nombre    = dto.title,
                                    imagenUrl = dto.coverImage  // real URL from Firestore
                                )
                            }
                        )
                    }

                _uiState.update {
                    it.copy(
                        artistas      = artistasAgrupados,
                        fotoPerfilUrl = PerfilData.perfilActual.fotoPerfilUrl,
                        isLoading     = false
                    )
                }
            } else {
                // Fallback to local data
                _uiState.update {
                    it.copy(
                        artistas      = HomeData.artistas,
                        fotoPerfilUrl = PerfilData.perfilActual.fotoPerfilUrl,
                        isLoading     = false,
                        errorMessage  = rawResult.exceptionOrNull()?.message
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

    fun getFirestoreId(hashId: Int): String? = _firestoreAlbumIds[hashId]
}