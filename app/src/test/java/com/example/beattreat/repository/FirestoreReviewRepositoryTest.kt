package com.example.beattreat.repository

import com.example.beattreat.data.datasource.FirestoreAlbumRemoteDataSource
import com.example.beattreat.data.datasource.FirestoreReviewRemoteDataSource
import com.example.beattreat.data.dto.FirestoreAlbumDto
import com.example.beattreat.data.dto.FirestoreReviewDto
import com.example.beattreat.data.dto.FirestoreReviewUserDto
import com.example.beattreat.data.dto.FirestoreUserDto
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.data.repository.FirestoreReviewRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Sprint 13 — Pruebas unitarias con mocks del FirestoreReviewRepository.
 * Verifica mapeos de DTOs a ResenaDetalladaUI y manejo de errores.
 */
class FirestoreReviewRepositoryTest {

    // ── Mocks ──────────────────────────────────────────────────────────────────
    private val mockReviewDataSource = mockk<FirestoreReviewRemoteDataSource>()
    private val mockUserRepository   = mockk<FirestoreUserRepository>()
    private val mockAlbumRepository  = mockk<FirestoreAlbumRepository>()
    private val mockAuth             = mockk<FirebaseAuth>()
    private val mockUser             = mockk<FirebaseUser>()

    private lateinit var repository: FirestoreReviewRepository

    // ── Datos de prueba ────────────────────────────────────────────────────────
    private val albumId   = "album_sprint13"
    private val reviewId  = "review_sprint13_001"
    private val userId    = "uid_sprint13"

    private fun makeFakeReviewDto(
        content: String  = "Álbum increíble",
        rating: Float    = 4.5f,
        likesCount: Int  = 7,
        userName: String = "Ana Beat",
        username: String = "anabeat"
    ) = FirestoreReviewDto(
        userId    = userId,
        albumId   = albumId,
        rating    = rating,
        content   = content,
        createdAt = 1_700_000_000_000L,
        likesCount = likesCount,
        user = FirestoreReviewUserDto(
            name         = userName,
            username     = username,
            profileImage = "https://example.com/foto.jpg"
        )
    )

    @Before
    fun setup() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid }         returns userId
        every { mockUser.displayName } returns "Ana Beat"
        every { mockUser.photoUrl }    returns null

        repository = FirestoreReviewRepository(
            dataSource             = mockReviewDataSource,
            userRepository         = mockUserRepository,
            firestoreAlbumRepository = mockAlbumRepository,
            firebaseAuth           = mockAuth
        )
    }

    // ── Prueba 1: Mapeo de rating ─────────────────────────────────────────────
    @Test
    fun `getReviewsByAlbum mapea rating del dto correctamente`() = runTest {
        val dto = makeFakeReviewDto(rating = 4.5f)
        coEvery { mockReviewDataSource.getReviewsByAlbum(albumId) } returns listOf(reviewId to dto)

        val result = repository.getReviewsByAlbum(albumId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.first()?.calificacion).isEqualTo(4.5f)
    }

    // ── Prueba 2: Mapeo de content / texto ────────────────────────────────────
    @Test
    fun `getReviewsByAlbum mapea content del dto a texto de UI`() = runTest {
        val dto = makeFakeReviewDto(content = "Producción impecable")
        coEvery { mockReviewDataSource.getReviewsByAlbum(albumId) } returns listOf(reviewId to dto)

        val result = repository.getReviewsByAlbum(albumId)

        assertThat(result.getOrNull()?.first()?.texto).isEqualTo("Producción impecable")
    }

    // ── Prueba 3: Mapeo del nombre del autor ──────────────────────────────────
    @Test
    fun `getReviewsByAlbum mapea nombre del usuario embebido en dto`() = runTest {
        val dto = makeFakeReviewDto(userName = "Carlos Rock")
        coEvery { mockReviewDataSource.getReviewsByAlbum(albumId) } returns listOf(reviewId to dto)

        val result = repository.getReviewsByAlbum(albumId)

        assertThat(result.getOrNull()?.first()?.autorNombre).isEqualTo("Carlos Rock")
    }

    // ── Prueba 4: Mapeo del username con @ ────────────────────────────────────
    @Test
    fun `getReviewsByAlbum mapea username con prefijo arroba`() = runTest {
        val dto = makeFakeReviewDto(username = "carlosrock")
        coEvery { mockReviewDataSource.getReviewsByAlbum(albumId) } returns listOf(reviewId to dto)

        val result = repository.getReviewsByAlbum(albumId)

        assertThat(result.getOrNull()?.first()?.autorUsuario).isEqualTo("@carlosrock")
    }

    // ── Prueba 5: Mapeo del ID del documento ──────────────────────────────────
    @Test
    fun `getReviewsByAlbum mapea el docId de Firestore al id de la UI`() = runTest {
        val dto = makeFakeReviewDto()
        coEvery { mockReviewDataSource.getReviewsByAlbum(albumId) } returns listOf(reviewId to dto)

        val result = repository.getReviewsByAlbum(albumId)

        assertThat(result.getOrNull()?.first()?.id).isEqualTo(reviewId)
        assertThat(result.getOrNull()?.first()?.firestoreDocId).isEqualTo(reviewId)
    }

    // ── Prueba 6: Álbum sin reviews devuelve lista vacía ──────────────────────
    @Test
    fun `getReviewsByAlbum con lista vacia retorna success con lista vacia`() = runTest {
        coEvery { mockReviewDataSource.getReviewsByAlbum("album_vacio") } returns emptyList()

        val result = repository.getReviewsByAlbum("album_vacio")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    // ── Prueba 7: Excepción del dataSource devuelve failure ───────────────────
    @Test
    fun `getReviewsByAlbum cuando datasource lanza excepcion retorna failure`() = runTest {
        coEvery { mockReviewDataSource.getReviewsByAlbum(any()) } throws
                RuntimeException("Error de red")

        val result = repository.getReviewsByAlbum(albumId)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("Error al cargar reviews")
    }

    // ── Prueba 8: createReview — usuario sin sesión devuelve failure ──────────
    @Test
    fun `createReview sin usuario autenticado retorna failure`() = runTest {
        every { mockAuth.currentUser } returns null

        val result = repository.createReview(albumId, 4.0f, "Gran álbum")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("sesión")
    }
}
