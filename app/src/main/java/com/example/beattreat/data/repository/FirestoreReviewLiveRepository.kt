// listenFeedMapItems: filtra reviews de las últimas 24h con coordenadas
// y los convierte a ReviewMapItem para renderizar en el mapa.

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
import com.example.beattreat.ui.FeedSiguiendo.ReviewMapItem

/**
Repositorio de reviews en tiempo real.

Sprint 4: agrega [listenFeedMapItems] que expone los mismos datos del feed
pero filtrados por las últimas 24h y con coordenadas válidas, listos
para ser pintados como marcadores en Google Maps.
*/

class FirestoreReviewLiveRepository @Inject constructor(
    private val liveDataSource: FirestoreReviewLiveDataSource,
    private val albumDataSource: FirestoreAlbumRemoteDataSource,
    private val userRepository: FirestoreUserRepository
) {

    companion object {
        /** 24 horas en milisegundos */
        private const val MILLIS_24H = 24L * 60L * 60L * 1_000L

        /** maximo de caracteres del texto en el InfoWindow */
        private const val MAX_TEXTO_PREVIEW = 120
    }

    // ── Lista de reviews ──────────────────────────────────────────

    fun listenReviewsByAlbum(albumId: String): Flow<List<ResenaDetalladaUI>> =
        liveDataSource.listenReviewsByAlbum(albumId).map { pairs ->
            val albumInfo: FirestoreAlbumDto? = try {
                albumDataSource.getAlbumById(albumId)
            } catch (e: Exception) { null }

            pairs.mapNotNull { (docId, dto) ->
                try {
                    val userInfo = userRepository.getUserById(dto.userId).getOrNull()
                    ResenaDetalladaUI(
                        id                   = docId,
                        albumId              = albumId,
                        autorNombre          = userInfo?.name ?: dto.user.name.ifBlank { "Usuario" },
                        autorUsuario         = userInfo?.username?.let { "@$it" } ?: dto.user.username,
                        autorFotoUrl         = userInfo?.profileImage?.takeIf { it.isNotBlank() }
                            ?: dto.user.profileImage?.takeIf { it.isNotBlank() } ?: "",
                        albumNombre          = albumInfo?.title ?: "",
                        albumArtista         = albumInfo?.artist ?: "",
                        albumImagenUrl       = albumInfo?.coverImage ?: "",
                        calificacion         = dto.rating,
                        texto                = dto.content,
                        likes                = dto.likesCount,
                        comentarios          = 0,
                        fecha                = formatTimestamp(dto.createdAt),
                        autorFirestoreUserId = dto.userId,
                        autorUserId          = 0,
                        firestoreDocId       = docId
                    )
                } catch (e: Exception) { null }
            }.sortedByDescending { it.fecha }
        }

    fun listenFeedByAuthors(authorIds: List<String>): Flow<List<ResenaDetalladaUI>> =
        liveDataSource.listenReviewsByAuthors(authorIds).map { pairs ->
            val albumsMap: Map<String, FirestoreAlbumDto> = try {
                albumDataSource.getAllAlbums()
            } catch (e: Exception) { emptyMap() }

            pairs.mapNotNull { (docId, dto) ->
                try {
                    val albumInfo = albumsMap[dto.albumId]
                    val userInfo  = userRepository.getUserById(dto.userId).getOrNull()
                    ResenaDetalladaUI(
                        id                   = docId,
                        albumId              = dto.albumId,
                        autorNombre          = userInfo?.name ?: dto.user.name.ifBlank { "Usuario" },
                        autorUsuario         = userInfo?.username?.let { "@$it" } ?: dto.user.username,
                        autorFotoUrl         = userInfo?.profileImage?.takeIf { it.isNotBlank() }
                            ?: dto.user.profileImage?.takeIf { it.isNotBlank() } ?: "",
                        albumNombre          = albumInfo?.title ?: "",
                        albumArtista         = albumInfo?.artist ?: "",
                        albumImagenUrl       = albumInfo?.coverImage ?: "",
                        calificacion         = dto.rating,
                        texto                = dto.content,
                        likes                = dto.likesCount,
                        comentarios          = 0,
                        fecha                = formatTimestamp(dto.createdAt),
                        autorFirestoreUserId = dto.userId,
                        autorUserId          = 0,
                        firestoreDocId       = docId
                    )
                } catch (e: Exception) { null }
            }.sortedByDescending { it.fecha }
        }

    // ── Flow de marcadores para el mapa ─────────────────────────────

    /**
     * Escucha el feed de [authorIds] y muestra solo los reviews que cumplen ambas condiciones:
     *   1. Fueron publicados en las últimas 24 horas.
     *   2. Tienen coordenadas GPS válidas (latitude != null && longitude != null).
     *
     * El Flow se actualiza automáticamente si llega un nuevo review con ubicación.
     */
    fun listenFeedMapItems(authorIds: List<String>): Flow<List<ReviewMapItem>> =
        liveDataSource.listenReviewsByAuthors(authorIds).map { pairs ->
            val cutoff    = System.currentTimeMillis() - MILLIS_24H
            val albumsMap = try { albumDataSource.getAllAlbums() } catch (e: Exception) { emptyMap() }

            pairs.mapNotNull { (docId, dto) ->
                //  últimas 24 horas
                if (dto.createdAt < cutoff) return@mapNotNull null

                // coordenadas válidas
                val lat = dto.latitude  ?: return@mapNotNull null
                val lng = dto.longitude ?: return@mapNotNull null

                // rangos geográficos validos
                if (lat !in -90.0..90.0 || lng !in -180.0..180.0) return@mapNotNull null

                val albumInfo = albumsMap[dto.albumId]
                val userInfo  = try {
                    userRepository.getUserById(dto.userId).getOrNull()
                } catch (e: Exception) { null }

                ReviewMapItem(
                    firestoreDocId = docId,
                    latitude       = lat,
                    longitude      = lng,
                    autorNombre    = userInfo?.name ?: dto.user.name.ifBlank { "Usuario" },
                    autorUsuario   = userInfo?.username?.let { "@$it" } ?: dto.user.username.let { "@$it" },
                    autorFotoUrl   = userInfo?.profileImage?.takeIf { it.isNotBlank() }
                        ?: dto.user.profileImage?.takeIf { it.isNotBlank() } ?: "",
                    albumNombre    = albumInfo?.title  ?: "Álbum desconocido",
                    albumArtista   = albumInfo?.artist ?: "",
                    albumImagenUrl = albumInfo?.coverImage ?: "",
                    calificacion   = dto.rating,
                    textoResumen   = dto.content.take(MAX_TEXTO_PREVIEW)
                        .let { if (dto.content.length > MAX_TEXTO_PREVIEW) "$it…" else it },
                    fecha          = formatTimestamp(dto.createdAt),
                    likes          = dto.likesCount
                )
            }
                // Ordenados por más reciente primero
                .sortedByDescending { pair ->
                    pairs.find { it.first == pair.firestoreDocId }?.second?.createdAt ?: 0L
                }
        }


    private fun formatTimestamp(ts: Long): String {
        if (ts == 0L) return ""
        return try {
            val now  = System.currentTimeMillis()
            val diff = now - ts
            when {
                diff < 60_000L      -> "Hace ${diff / 1_000} segundos"
                diff < 3_600_000L   -> "Hace ${diff / 60_000} minutos"
                diff < 86_400_000L  -> "Hace ${diff / 3_600_000} horas"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
            }
        } catch (e: Exception) { "" }
    }
}