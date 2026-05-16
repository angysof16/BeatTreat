package com.example.beattreat.data.datasource.implementation.firestore

import com.example.beattreat.data.datasource.CommentFirestoreDataSource
import com.example.beattreat.data.dto.FirestoreCommentDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : CommentFirestoreDataSource {

    companion object {
        private const val COMMENTS_COL = "comments"
    }

    override fun listenCommentsByReview(reviewId: String): Flow<List<Pair<String, FirestoreCommentDto>>> =
        callbackFlow {
            val listener = db.collection(COMMENTS_COL)
                .whereEqualTo("reviewId", reviewId)
                // BUG FIX: se elimina .orderBy("createdAt") porque requiere un índice
                // compuesto en Firestore (reviewId + createdAt) que probablemente no existe.
                // Sin el índice, Firestore lanza una excepción que cierra el Flow silenciosamente,
                // por lo que los comentarios nunca aparecen y tampoco se pueden publicar.
                //
                // Solución: ordenamos en memoria después de recibir los datos.
                // Si quieres restaurar el orderBy de Firestore, crea el índice en la consola:
                //   Firebase Console → Firestore → Indexes → Add Index:
                //   Collection: comments | Fields: reviewId ASC, createdAt ASC
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        android.util.Log.e("CommentDataSource", "Error en listener: ${error.message}")
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val comments = snapshot.documents.mapNotNull { doc ->
                            val dto = doc.toObject(FirestoreCommentDto::class.java) ?: return@mapNotNull null
                            doc.id to dto
                        }
                        // Ordenar por createdAt en memoria (más reciente al final)
                        val sorted = comments.sortedBy { it.second.createdAt }
                        trySend(sorted)
                    }
                }
            awaitClose { listener.remove() }
        }

    override suspend fun addComment(dto: FirestoreCommentDto): String {
        return try {
            val ref = db.collection(COMMENTS_COL).add(dto).await()
            android.util.Log.d("CommentDataSource", "Comentario agregado: ${ref.id}")
            ref.id
        } catch (e: Exception) {
            android.util.Log.e("CommentDataSource", "Error al agregar comentario: ${e.message}")
            throw e
        }
    }
}