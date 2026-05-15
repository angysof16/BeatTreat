package com.example.beattreat.repository

import com.example.beattreat.data.datasource.FollowRemoteDataSource
import com.example.beattreat.data.dto.FirestoreUserDto
import com.example.beattreat.data.repository.FollowRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Sprint 13 — Pruebas unitarias con mocks del FollowRepository.
 * Verifica mapeos a UsuarioUI y manejo de errores.
 */
class FollowRepositoryTest {

    private val mockDataSource = mockk<FollowRemoteDataSource>()
    private lateinit var repository: FollowRepository

    private val currentUserId = "uid_yo"
    private val targetUserId  = "uid_otro"

    @Before
    fun setup() {
        repository = FollowRepository(mockDataSource)
    }

    // ── Prueba 1: followOrUnfollow retorna true cuando pasa a seguir ──────────
    @Test
    fun `followOrUnfollow cuando era unfollowed retorna success true`() = runTest {
        coEvery { mockDataSource.followOrUnfollow(currentUserId, targetUserId) } returns true

        val result = repository.followOrUnfollow(currentUserId, targetUserId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
    }

    // ── Prueba 2: followOrUnfollow retorna false cuando deja de seguir ────────
    @Test
    fun `followOrUnfollow cuando era followed retorna success false`() = runTest {
        coEvery { mockDataSource.followOrUnfollow(currentUserId, targetUserId) } returns false

        val result = repository.followOrUnfollow(currentUserId, targetUserId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isFalse()
    }

    // ── Prueba 3: followOrUnfollow — excepción devuelve failure ───────────────
    @Test
    fun `followOrUnfollow cuando datasource lanza excepcion retorna failure`() = runTest {
        coEvery { mockDataSource.followOrUnfollow(any(), any()) } throws
                RuntimeException("Sin conexión")

        val result = repository.followOrUnfollow(currentUserId, targetUserId)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("follow")
    }

    // ── Prueba 4: isFollowing retorna true cuando sigue ───────────────────────
    @Test
    fun `isFollowing cuando usuario sigue retorna success true`() = runTest {
        coEvery { mockDataSource.isFollowing(currentUserId, targetUserId) } returns true

        val result = repository.isFollowing(currentUserId, targetUserId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
    }

    // ── Prueba 5: getFollowersCount mapea el número correcto ──────────────────
    @Test
    fun `getFollowersCount retorna el numero de seguidores del datasource`() = runTest {
        coEvery { mockDataSource.getFollowersCount(targetUserId) } returns 42

        val result = repository.getFollowersCount(targetUserId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(42)
    }

    // ── Prueba 6: getFollowingCount mapea el número correcto ──────────────────
    @Test
    fun `getFollowingCount retorna el numero de seguidos del datasource`() = runTest {
        coEvery { mockDataSource.getFollowingCount(currentUserId) } returns 15

        val result = repository.getFollowingCount(currentUserId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(15)
    }

    // ── Prueba 7: getFollowersAsUI mapea firestoreId correctamente ────────────
    @Test
    fun `getFollowersAsUI mapea firestoreId real de cada seguidor`() = runTest {
        val ids  = listOf("uid_fan1", "uid_fan2")
        val dtos = listOf(
            FirestoreUserDto(username = "fan1", name = "Fan Uno"),
            FirestoreUserDto(username = "fan2", name = "Fan Dos")
        )
        coEvery { mockDataSource.getFollowerIds(currentUserId) } returns ids
        coEvery { mockDataSource.getFollowersUsers(currentUserId) } returns dtos

        val result = repository.getFollowersAsUI(currentUserId)

        assertThat(result.isSuccess).isTrue()
        val lista = result.getOrNull()!!
        assertThat(lista[0].firestoreId).isEqualTo("uid_fan1")
        assertThat(lista[1].firestoreId).isEqualTo("uid_fan2")
    }

    // ── Prueba 8: getFollowingAsUI mapea username con @ ───────────────────────
    @Test
    fun `getFollowingAsUI mapea username con prefijo arroba`() = runTest {
        val ids  = listOf("uid_artista")
        val dtos = listOf(FirestoreUserDto(username = "artista99", name = "Artista Noventa"))
        coEvery { mockDataSource.getFollowingIds(currentUserId) }    returns ids
        coEvery { mockDataSource.getFollowingUsers(currentUserId) }  returns dtos

        val result = repository.getFollowingAsUI(currentUserId)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.first()?.usuario).isEqualTo("@artista99")
    }
}
