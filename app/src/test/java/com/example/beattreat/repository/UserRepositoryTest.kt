package com.example.beattreat.repository

import com.example.beattreat.data.datasource.FirestoreUserRemoteDataSource
import com.example.beattreat.data.dto.FirestoreUserDto
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


class UserRepositoryTest {

    private val mockDataSource  = mockk<FirestoreUserRemoteDataSource>()
    private val mockAuth        = mockk<FirebaseAuth>()
    private val mockUser        = mockk<FirebaseUser>()
    private lateinit var repository: FirestoreUserRepository

    @Before
    fun setup() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "uid_test_123"
        every { mockUser.photoUrl } returns null
        every { mockUser.displayName } returns "Test User"
        repository = FirestoreUserRepository(mockDataSource, mockAuth)
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun `getUserById con id valido retorna Result success con datos correctos`() = runTest {
        val dto = FirestoreUserDto(
            username     = "beatfan99",
            name         = "Juan Beat",
            country      = "Colombia",
            bio          = "Amante de la música",
            profileImage = "https://example.com/foto.jpg"
        )
        coEvery { mockDataSource.getUserById("uid_test_123") } returns dto

        val result = repository.getUserById("uid_test_123")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.name).isEqualTo("Juan Beat")
        assertThat(result.getOrNull()?.username).isEqualTo("beatfan99")
    }

    @Test
    fun `getUserById cuando datasource lanza excepcion retorna Result failure`() = runTest {
        val exception = RuntimeException("Usuario no encontrado")
        coEvery { mockDataSource.getUserById("uid_invalido") } throws exception

        val result = repository.getUserById("uid_invalido")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("no encontrado")
    }

    @Test
    fun `getUserById con bio nula retorna bio como null en el dto`() = runTest {
        val dto = FirestoreUserDto(
            username     = "sinbio",
            name         = "Sin Bio",
            country      = null,
            bio          = null,
            profileImage = null
        )
        coEvery { mockDataSource.getUserById("uid_sinbio") } returns dto

        val result = repository.getUserById("uid_sinbio")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.bio).isNull()
        assertThat(result.getOrNull()?.profileImage).isNull()
    }

    @Test
    fun `getUserById cuando usuario no existe retorna failure con mensaje`() = runTest {
        coEvery { mockDataSource.getUserById("uid_9999") } throws
                NoSuchElementException("Usuario uid_9999 no encontrado")

        val result = repository.getUserById("uid_9999")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(Exception::class.java)
    }

    @Test
    fun `getCurrentUserId retorna el uid del usuario autenticado`() {
        val uid = repository.getCurrentUserId()
        assertThat(uid).isEqualTo("uid_test_123")
    }

    @Test
    fun `getCurrentUserId retorna null cuando no hay usuario autenticado`() {
        every { mockAuth.currentUser } returns null
        val uid = repository.getCurrentUserId()
        assertThat(uid).isNull()
    }

    @Test
    fun `getUserById con country retorna el pais correcto`() = runTest {
        val dto = FirestoreUserDto(
            username     = "colombiano",
            name         = "Carlos Ruiz",
            country      = "Colombia",
            bio          = "Fan del vallenato",
            profileImage = null
        )
        coEvery { mockDataSource.getUserById("uid_col") } returns dto

        val result = repository.getUserById("uid_col")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.country).isEqualTo("Colombia")
    }

    @Test
    fun `getUserById con profileImage retorna la url correcta`() = runTest {
        val fotoUrl = "https://storage.firebase.com/foto_perfil.jpg"
        val dto = FirestoreUserDto(
            username     = "confoto",
            name         = "Con Foto",
            country      = "Argentina",
            bio          = "Con foto de perfil",
            profileImage = fotoUrl
        )
        coEvery { mockDataSource.getUserById("uid_foto") } returns dto

        val result = repository.getUserById("uid_foto")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.profileImage).isEqualTo(fotoUrl)
    }
}
