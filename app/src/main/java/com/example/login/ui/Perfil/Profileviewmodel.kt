package com.example.login.ui.Perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.example.login.data.repository.FirestoreAlbumRepository
import com.example.login.data.repository.FirestoreReviewRepository
import com.example.login.data.repository.FirestoreUserRepository
import com.example.login.data.repository.StorageRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreUserRepository: FirestoreUserRepository,
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val firestoreAlbumRepository: FirestoreAlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
        cargarResenasFirestore()
    }

    private fun cargarPerfil() {
        viewModelScope.launch {
            // Try Firestore first, fallback to FirebaseAuth photo
            val result = firestoreUserRepository.getMyProfile()
            val perfil = result.getOrElse {
                // Fallback: use local data with FirebaseAuth photo
                val urlDeFirebaseAuth = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""
                PerfilData.perfilActual.copy(fotoPerfilUrl = urlDeFirebaseAuth)
            }
            PerfilData.perfilActual = perfil
            _uiState.update {
                it.copy(
                    perfil           = PerfilData.perfilActual,
                    albumesFavoritos = PerfilData.albumesFavoritos,
                    isLoading        = false
                )
            }
        }
    }

    private fun cargarResenasFirestore() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            // Load reviews and album data in parallel
            val reviewsDeferred = async { firestoreReviewRepository.getReviewsByUser(userId) }
            val albumsDeferred  = async { firestoreAlbumRepository.getAllAlbumsRaw() }

            val reviewsResult = reviewsDeferred.await()
            val albumsResult  = albumsDeferred.await()
            val albumsMap     = albumsResult.getOrDefault(emptyMap())

            reviewsResult.onSuccess { resenas ->
                val resenasUI = resenas.take(3).map { resena ->
                    // Find album info from the map using albumId (which is a Firestore doc ID)
                    val albumDto = albumsMap[resena.albumId.toString()]
                        ?: albumsMap.entries.find { it.key.hashCode().toString() == resena.albumId.toString() }?.value

                    ResenaConAlbumUI(
                        id           = resena.id,
                        autorNombre  = PerfilData.perfilActual.nombre,
                        autorUsuario = PerfilData.perfilActual.usuario,
                        autorFotoUrl = PerfilData.perfilActual.fotoPerfilUrl,
                        texto        = resena.texto,
                        likes        = 0,
                        comentarios  = 0,
                        albumNombre  = albumDto?.title ?: resena.albumNombre,
                        albumArtista = albumDto?.artist ?: resena.albumArtista,
                        albumCover   = albumDto?.coverImage ?: resena.albumImagenUrl,
                        calificacion = resena.calificacion
                    )
                }
                _uiState.update { it.copy(resenasConAlbum = resenasUI) }
            }
        }
    }

    fun refrescarPerfil() {
        cargarPerfil()
        cargarResenasFirestore()
    }

    fun refrescarFotoPerfil() {
        val urlFirebaseAuth = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""
        val urlFinal = urlFirebaseAuth.ifBlank { PerfilData.perfilActual.fotoPerfilUrl }
        _uiState.update { state ->
            state.copy(perfil = state.perfil?.copy(fotoPerfilUrl = urlFinal))
        }
    }

    fun cerrarSesion() {
        PerfilData.perfilActual = PerfilData.perfilActual.copy(fotoPerfilUrl = "")
        authRepository.signOut()
        _uiState.update { it.copy(cerrarSesionExitoso = true) }
    }

    fun resetCerrarSesion() {
        _uiState.update { it.copy(cerrarSesionExitoso = false) }
    }
}