package com.example.login.data.datasource.implementation.firestore

import com.example.login.data.datasource.FirestoreReviewLiveDataSource
import com.example.login.data.dto.FirestoreReviewDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Implementación de reviews en tiempo real usando callbackFlow.
 *
 * El profesor explicó exactamente esta sintaxis:
 *
 *   return callbackFlow {
 *       val listener = db.collection("reviews")
 *           .addSnapshotListener { snapshot, error ->
 *               if (error != null) { close(error); return@addSnapshotListener }
 *               if (snapshot != null) {
 *                   val items = snapshot.documents.mapNotNull { ... }
 *                   trySend(items)   // envía los datos al Flow
 *               }
 *           }
 *       awaitClose { listener.remove() }  // cancela cuando nadie escucha
 *   }
 *
 * IMPORTANTE (dicho por el profesor):
 *   - Si cambia un documento en la colección → el Flow se actualiza.
 *   - Si cambia una SUBCOLECCIÓN (ej: likes) → el Flow NO se actualiza
 *     a menos que también cambie el documento padre (likesCount sí lo hace).
 *   - Esto es bueno porque controla cuándo queremos actualizaciones.
 */
class ReviewLiveFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirestoreReviewLiveDataSource {

    companion object {
        private const val REVIEWS_COL = "reviews"
    }

    /**
     * Escucha todas las reviews de un álbum en tiempo real.
     * Cada vez que se crea, edita o elimina una review del álbum,
     * el Flow emite la nueva lista.
     */
    override fun listenReviewsByAlbum(albumId: String): Flow<List<Pair<String, FirestoreReviewDto>>> =
        callbackFlow {
            val listener = db.collection(REVIEWS_COL)
                .whereEqualTo("albumId", albumId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val reviews = snapshot.documents.mapNotNull { doc ->
                            val dto = doc.toObject(FirestoreReviewDto::class.java)
                                ?: return@mapNotNull null
                            doc.id to dto
                        }
                        trySend(reviews)
                    }
                }
            // Cancela la conexión en tiempo real cuando nadie escucha
            awaitClose { listener.remove() }
        }

    /**
     * Escucha reviews de una lista de autores (feed "siguiendo").
     * Emite cuando cualquier review de esos autores cambia.
     *
     * NOTA: Firestore limita whereIn a 10 elementos.
     * Si el usuario sigue a más de 10 personas, se hacen múltiples queries.
     */
    override fun listenReviewsByAuthors(
        authorIds: List<String>
    ): Flow<List<Pair<String, FirestoreReviewDto>>> = callbackFlow {

        if (authorIds.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        // Firestore limita whereIn a máximo 10 valores
        val chunks = authorIds.distinct().chunked(10)
        val allReviews = mutableMapOf<String, FirestoreReviewDto>()
        val listeners  = mutableListOf<com.google.firebase.firestore.ListenerRegistration>()

        chunks.forEach { chunk ->
            val listener = db.collection(REVIEWS_COL)
                .whereIn("userId", chunk)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        snapshot.documents.forEach { doc ->
                            val dto = doc.toObject(FirestoreReviewDto::class.java)
                            if (dto != null) allReviews[doc.id] = dto
                        }
                        // Ordena por fecha descendente antes de emitir
                        val sorted = allReviews.entries
                            .sortedByDescending { it.value.createdAt }
                            .map { it.key to it.value }
                        trySend(sorted)
                    }
                }
            listeners.add(listener)
        }

        awaitClose { listeners.forEach { it.remove() } }
    }
}
