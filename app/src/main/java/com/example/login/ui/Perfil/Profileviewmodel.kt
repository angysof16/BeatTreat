package com.example.login.ui.Perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.example.login.data.repository.FollowRepository
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

/**
 * FIX Sprint 3 — Bug #1
 *
 * Problema original: los contadores de seguidores y siguiendo usaban
 * los valores hardcodeados de PerfilData (siguiendo: 127, seguidores: 89)
 * porque ProfileViewModel nunca llamaba a FollowRepository.
 *
 * Solución: se inyecta FollowRepository y se cargan los contadores reales
 * de Firestore junto con el resto del perfil.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreUserRepository: FirestoreUserRepository,
    private val firestoreReviewRepository: FirestoreReviewRepository,
    private val firestoreAlbumRepository: FirestoreAlbumRepository,
    // FIX: inyección del repositorio de follows para contadores reales
    private val followRepository: FollowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    init {
        cargarPerfil()
        cargarResenasFirestore()
    }

    private fun cargarPerfil() {
        viewModelScope.launch {
            // FIX: carga contadores reales en paralelo con el perfil
            val perfilDeferred    = async { firestoreUserRepository.getMyProfile() }
            val followersDeferred = async {
                if (currentUserId.isNotBlank())
                    followRepository.getFollowersCount(currentUserId).getOrNull() ?: 0
                else 0
            }
            val followingDeferred = async {
                if (currentUserId.isNotBlank())
                    followRepository.getFollowingCount(currentUserId).getOrNull() ?: 0
                else 0
            }

            val perfilResult   = perfilDeferred.await()
            val followersCount = followersDeferred.await()
            val followingCount = followingDeferred.await()

            val perfil = perfilResult.getOrElse {
                val urlDeFirebaseAuth = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""
                PerfilData.perfilActual.copy(fotoPerfilUrl = urlDeFirebaseAuth)
            }

            // FIX: sobreescribe los contadores hardcodeados con los reales de Firestore
            val perfilConContadores = perfil.copy(
                seguidores = followersCount,
                siguiendo  = followingCount
            )

            PerfilData.perfilActual = perfilConContadores
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
        if (currentUserId.isBlank()) return

        viewModelScope.launch {
            val reviewsDeferred = async { firestoreReviewRepository.getReviewsByUser(currentUserId) }
            val albumsDeferred  = async { firestoreAlbumRepository.getAllAlbumsRaw() }

            val reviewsResult = reviewsDeferred.await()
            val albumsResult  = albumsDeferred.await()
            val albumsMap     = albumsResult.getOrDefault(emptyMap())

            reviewsResult.onSuccess { resenas ->
                val resenasUI = resenas.take(3).map { resena ->
                    val albumDto = albumsMap[resena.albumId.toString()]
                        ?: albumsMap.entries.find {
                            it.key.hashCode().toString() == resena.albumId.toString()
                        }?.value

                    ResenaConAlbumUI(
                        id           = resena.id,
                        autorNombre  = PerfilData.perfilActual.nombre,
                        autorUsuario = PerfilData.perfilActual.usuario,
                        autorFotoUrl = PerfilData.perfilActual.fotoPerfilUrl,
                        texto        = resena.texto,
                        likes        = 0,
                        comentarios  = 0,
                        albumNombre  = albumDto?.title  ?: resena.albumNombre,
                        albumArtista = albumDto?.artist ?: resena.albumArtista,
                        albumCover   = albumDto?.coverImage ?: resena.albumImagenUrl,
                        calificacion = resena.calificacion
                    )
                }
                _uiState.update { it.copy(resenasConAlbum = resenasUI) }
            }
        }
    }

    /**
     * FIX: refrescarPerfil también recarga los contadores de followers/following.
     */
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
