package com.example.login.data.datasource

import com.example.login.data.dto.FirestoreReviewDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source para escuchar reviews en tiempo real con Flow.
 *
 * Exactamente como explicó el profesor:
 *   - En vez de retornar List<FirestoreReviewDto> (datos estáticos),
 *     retornamos Flow<List<FirestoreReviewDto>> (datos en vivo).
 *   - Internamente usa addSnapshotListener de Firestore.
 *   - El Flow se cancela automáticamente cuando nadie escucha
 *     (callbackFlow + awaitClose { listener.remove() }).
 *
 * listenReviewsByAlbum:   escucha reviews de un álbum en tiempo real.
 * listenReviewsByAuthors: escucha reviews de usuarios específicos (feed).
 */
interface FirestoreReviewLiveDataSource {
    fun listenReviewsByAlbum(albumId: String): Flow<List<Pair<String, FirestoreReviewDto>>>
    fun listenReviewsByAuthors(authorIds: List<String>): Flow<List<Pair<String, FirestoreReviewDto>>>
}
