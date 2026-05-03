// data/repository/FirestoreReviewLiveRepository.kt
package com.example.beattreat.data.repository

import com.example.beattreat.data.datasource.FirestoreAlbumRemoteDataSource
import com.example.beattreat.data.datasource.FirestoreReviewLiveDataSource
import com.example.beattreat.data.dto.FirestoreAlbumDto
import com.example.beattreat.ui.Resena.ResenaDetalladaUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Repositorio de reviews en tiempo real.
 */
class FirestoreReviewLiveRepository @Inject constructor(
    private val liveDataSource: FirestoreReviewLiveDataSource,
    private val albumDataSource: FirestoreAlbumRemoteDataSource,  // ← Cambiar a FirestoreAlbumRemoteDataSource
    private val userRepository: FirestoreUserRepository
) {

    /**
     * Escucha reviews de un álbum en tiempo real.
     * Retorna Flow que se actualiza automáticamente cuando cambia Firestore.
     */
    fun listenReviewsByAlbum(albumId: String): Flow<List<ResenaDetalladaUI>> =
        liveDataSource.listenReviewsByAlbum(albumId).map { pairs ->
            // Obtener información del álbum directamente del data source
            val albumInfo: FirestoreAlbumDto? = try {
                albumDataSource.getAlbumById(albumId)
            } catch (e: Exception) {
                null
            }

            pairs.mapNotNull { (docId, dto) ->
                try {
                    val userInfo = userRepository.getUserById(dto.userId).getOrNull()

                    ResenaDetalladaUI(
                        id = docId,
                        albumId = albumId,
                        autorNombre = userInfo?.name ?: dto.user.name.ifBlank { "Usuario" },
                        autorUsuario = userInfo?.username?.let { "@$it" } ?: dto.user.username,
                        autorFotoUrl = userInfo?.profileImage ?: dto.user.profileImage ?: "",
                        albumNombre = albumInfo?.title ?: "",  // ← title de FirestoreAlbumDto
                        albumArtista = albumInfo?.artist ?: "",  // ← artist de FirestoreAlbumDto
                        albumImagenUrl = albumInfo?.coverImage ?: "",  // ← coverImage de FirestoreAlbumDto
                        calificacion = dto.rating,
                        texto = dto.content,
                        likes = dto.likesCount,
                        comentarios = 0,
                        fecha = formatTimestamp(dto.createdAt),
                        autorFirestoreUserId = dto.userId,
                        autorUserId = 0,
                        firestoreDocId = docId
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.fecha }
        }

    fun listenFeedByAuthors(authorIds: List<String>): Flow<List<ResenaDetalladaUI>> =
        liveDataSource.listenReviewsByAuthors(authorIds).map { pairs ->
            // Obtener todos los álbumes del data source
            val albumsMap: Map<String, FirestoreAlbumDto> = try {
                albumDataSource.getAllAlbums()
            } catch (e: Exception) {
                emptyMap()
            }

            pairs.mapNotNull { (docId, dto) ->
                try {
                    val albumInfo = albumsMap[dto.albumId]
                    val userInfo = userRepository.getUserById(dto.userId).getOrNull()

                    ResenaDetalladaUI(
                        id = docId,
                        albumId = dto.albumId,
                        autorNombre = userInfo?.name ?: dto.user.name.ifBlank { "Usuario" },
                        autorUsuario = userInfo?.username?.let { "@$it" } ?: dto.user.username,
                        autorFotoUrl = userInfo?.profileImage ?: dto.user.profileImage ?: "",
                        albumNombre = albumInfo?.title ?: "",
                        albumArtista = albumInfo?.artist ?: "",
                        albumImagenUrl = albumInfo?.coverImage ?: "",
                        calificacion = dto.rating,
                        texto = dto.content,
                        likes = dto.likesCount,
                        comentarios = 0,
                        fecha = formatTimestamp(dto.createdAt),
                        autorFirestoreUserId = dto.userId,
                        autorUserId = 0,
                        firestoreDocId = docId
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.fecha }
        }

    private fun formatTimestamp(ts: Long): String {
        if (ts == 0L) return ""
        return try {
            val now = System.currentTimeMillis()
            val diff = now - ts

            when {
                diff < 60000 -> "Hace ${diff / 1000} segundos"
                diff < 3600000 -> "Hace ${diff / 60000} minutos"
                diff < 86400000 -> "Hace ${diff / 3600000} horas"
                else -> {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
                }
            }
        } catch (e: Exception) { "" }
    }
}