package com.example.login.data.repository

import com.example.login.data.datasource.FirestoreReviewLiveDataSource
import com.example.login.data.repository.FirestoreAlbumRepository
import com.example.login.ui.Resena.ResenaDetalladaUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Repositorio de reviews en tiempo real.
 *
 * Mapea el Flow<List<Pair<String, FirestoreReviewDto>>> del data source
 * a un Flow<List<ResenaDetalladaUI>> que consume la UI.
 *
 * NOTA sobre Flows (explicado por el profesor):
 *   - No lanzamos excepciones en try-catch, porque cuando el Flow se cancela
 *     no es una excepción sino que el flujo simplemente para.
 *   - Los errores se manejan con .catch { } en el ViewModel.
 *   - El Flow se cancela automáticamente cuando el ViewModel se destruye.
 */
class FirestoreReviewLiveRepository @Inject constructor(
    private val liveDataSource: FirestoreReviewLiveDataSource,
    private val albumRepository: FirestoreAlbumRepository
) {

    /**
     * Escucha reviews de un álbum en tiempo real.
     * Retorna Flow que se actualiza automáticamente cuando cambia Firestore.
     */
    fun listenReviewsByAlbum(albumId: String): Flow<List<ResenaDetalladaUI>> =
        liveDataSource.listenReviewsByAlbum(albumId).map { pairs ->
            pairs.map { (docId, dto) ->
                ResenaDetalladaUI(
                    id                   = docId.hashCode(),
                    albumId              = albumId.hashCode(),
                    autorNombre          = dto.user.name.ifBlank { "Usuario" },
                    autorUsuario         = "@${dto.user.username}",
                    autorFotoUrl         = dto.user.profileImage ?: "",
                    albumNombre          = "",
                    albumArtista         = "",
                    albumImagenUrl       = "",
                    calificacion         = dto.rating,
                    texto                = dto.content,
                    likes                = 0,
                    comentarios          = 0,
                    fecha                = formatTimestamp(dto.createdAt),
                    autorFirestoreUserId = dto.userId,
                    autorUserId          = 1,
                    firestoreDocId       = docId
                )
            }
        }

    /**
     * Escucha el feed "siguiendo": reviews de usuarios a los que sigo.
     * Se actualiza en tiempo real cuando cualquier seguido publica o edita.
     */
    fun listenFeedByAuthors(authorIds: List<String>): Flow<List<ResenaDetalladaUI>> =
        liveDataSource.listenReviewsByAuthors(authorIds).map { pairs ->
            pairs.map { (docId, dto) ->
                ResenaDetalladaUI(
                    id                   = docId.hashCode(),
                    albumId              = dto.albumId.hashCode(),
                    autorNombre          = dto.user.name.ifBlank { "Usuario" },
                    autorUsuario         = "@${dto.user.username}",
                    autorFotoUrl         = dto.user.profileImage ?: "",
                    albumNombre          = "",
                    albumArtista         = "",
                    albumImagenUrl       = "",
                    calificacion         = dto.rating,
                    texto                = dto.content,
                    likes                = 0,
                    comentarios          = 0,
                    fecha                = formatTimestamp(dto.createdAt),
                    autorFirestoreUserId = dto.userId,
                    autorUserId          = 1,
                    firestoreDocId       = docId
                )
            }
        }

    private fun formatTimestamp(ts: Long): String {
        if (ts == 0L) return ""
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
        } catch (e: Exception) { "" }
    }
}
