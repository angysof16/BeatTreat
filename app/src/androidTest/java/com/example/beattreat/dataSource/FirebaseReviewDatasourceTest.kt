package com.example.beattreat.dataSource

import com.example.beattreat.data.datasource.implementation.firestore.ReviewFirestoreDataSourceImpl
import com.example.beattreat.data.dto.FirestoreReviewDto
import com.example.beattreat.data.dto.FirestoreReviewUserDto
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Sprint 12 — Prueba de integración del ReviewFirestoreDataSource.
 * Conecta al emulador de Firestore (10.0.2.2:8080).
 */
class FirebaseReviewDatasourceTest {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var dataSource: ReviewFirestoreDataSourceImpl

    private val testAlbumId = "test_album_review_ds"
    private val testUserId  = "test_user_review_ds"
    private val testUserId2 = "test_user_review_ds_2"

    private fun makeFakeReview(
        userId: String  = testUserId,
        albumId: String = testAlbumId,
        rating: Float   = 4.5f,
        content: String = "Excelente álbum de prueba"
    ) = FirestoreReviewDto(
        userId    = userId,
        albumId   = albumId,
        rating    = rating,
        content   = content,
        createdAt = System.currentTimeMillis(),
        likesCount = 0,
        user = FirestoreReviewUserDto(
            name         = "Test Beat User",
            username     = "testbeatuser",
            profileImage = null
        )
    )

    @Before
    fun setup() = runTest {
        try {
            db.useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) { /* ya configurado */ }

        dataSource = ReviewFirestoreDataSourceImpl(db)

        // Limpiar reviews antes de cada test
        val reviews = db.collection("reviews").get().await()
        for (doc in reviews) doc.reference.delete().await()

        // Insertar reviews de prueba (seed)
        val seedReviews = listOf(
            mapOf(
                "userId"    to testUserId,
                "albumId"   to testAlbumId,
                "rating"    to 4.0f,
                "content"   to "Review seed 1 del álbum de prueba",
                "createdAt" to System.currentTimeMillis(),
                "likesCount" to 3,
                "user" to mapOf("name" to "Test Beat User", "username" to "testbeatuser", "profileImage" to null)
            ),
            mapOf(
                "userId"    to testUserId2,
                "albumId"   to testAlbumId,
                "rating"    to 5.0f,
                "content"   to "Review seed 2 del álbum de prueba",
                "createdAt" to System.currentTimeMillis() - 3600000,
                "likesCount" to 10,
                "user" to mapOf("name" to "Otro User", "username" to "otrouser", "profileImage" to null)
            )
        )
        for (data in seedReviews) {
            db.collection("reviews").add(data).await()
        }
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun getReviewsByAlbum_albumConReviews_retornaListaCorrecta() = runTest {
        val reviews = dataSource.getReviewsByAlbum(testAlbumId)

        assertThat(reviews).isNotNull()
        assertThat(reviews.size).isEqualTo(2)
        reviews.forEach { (docId, dto) ->
            assertThat(docId).isNotEmpty()
            assertThat(dto.albumId).isEqualTo(testAlbumId)
        }
    }

    @Test
    fun getReviewsByAlbum_albumSinReviews_listaVacia() = runTest {
        val reviews = dataSource.getReviewsByAlbum("album_sin_reviews_xyz_999")

        assertThat(reviews).isNotNull()
        assertThat(reviews).isEmpty()
    }

    @Test
    fun createReview_retornaIdNoVacio_yDocumentoExisteEnFirestore() = runTest {
        val dto   = makeFakeReview(content = "Review creada en test createReview")
        val docId = dataSource.createReview(dto)

        assertThat(docId).isNotEmpty()

        val doc = db.collection("reviews").document(docId).get().await()
        assertThat(doc.exists()).isTrue()
        assertThat(doc.getString("content")).isEqualTo("Review creada en test createReview")
    }

    @Test
    fun createReview_inicializaLikesCountEnCero() = runTest {
        val dto   = makeFakeReview(content = "Test likes count init")
        val docId = dataSource.createReview(dto)

        val doc = db.collection("reviews").document(docId).get().await()
        val likesCount = doc.getLong("likesCount") ?: -1L
        assertThat(likesCount).isEqualTo(0L)
    }

    @Test
    fun getReviewsByUser_retornaSoloReviewsDelUsuario() = runTest {
        val reviews = dataSource.getReviewsByUser(testUserId)

        assertThat(reviews).isNotNull()
        assertThat(reviews.size).isEqualTo(1)
        reviews.forEach { (_, dto) ->
            assertThat(dto.userId).isEqualTo(testUserId)
        }
    }

    @Test
    fun deleteReview_eliminaDocumentoDeFirestore() = runTest {
        // Crear una review para eliminar
        val dto   = makeFakeReview(content = "Esta review será eliminada")
        val docId = dataSource.createReview(dto)

        // Confirmar que existe
        val antes = db.collection("reviews").document(docId).get().await()
        assertThat(antes.exists()).isTrue()

        // Eliminar
        dataSource.deleteReview(docId)

        // Confirmar que ya no existe
        val despues = db.collection("reviews").document(docId).get().await()
        assertThat(despues.exists()).isFalse()
    }

    @Test
    fun updateReview_actualizaRatingYContent() = runTest {
        val dto   = makeFakeReview(rating = 2.0f, content = "Contenido original")
        val docId = dataSource.createReview(dto)

        dataSource.updateReview(docId, 5.0f, "Contenido completamente actualizado")

        val doc = db.collection("reviews").document(docId).get().await()
        assertThat((doc.get("rating") as? Number)?.toFloat()).isEqualTo(5.0f)
        assertThat(doc.getString("content")).isEqualTo("Contenido completamente actualizado")
    }

    @Test
    fun createReview_guardaUserIdYAlbumIdCorrectamente() = runTest {
        val userId  = "user_especifico_123"
        val albumId = "album_especifico_456"
        val dto     = makeFakeReview(userId = userId, albumId = albumId)
        val docId   = dataSource.createReview(dto)

        val doc = db.collection("reviews").document(docId).get().await()
        assertThat(doc.getString("userId")).isEqualTo(userId)
        assertThat(doc.getString("albumId")).isEqualTo(albumId)
    }

    @Test
    fun getReviewsByAlbum_retornaPairConDocIdNoVacio() = runTest {
        val reviews = dataSource.getReviewsByAlbum(testAlbumId)

        reviews.forEach { (docId, dto) ->
            assertThat(docId).isNotEmpty()
            assertThat(dto.rating).isGreaterThan(0f)
            assertThat(dto.content).isNotEmpty()
        }
    }
}
