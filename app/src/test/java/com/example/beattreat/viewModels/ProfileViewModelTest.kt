package com.example.beattreat.viewModels

import com.example.beattreat.data.repository.AuthRepository
import com.example.beattreat.data.repository.FirestoreAlbumRepository
import com.example.beattreat.data.repository.FirestoreReviewRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.example.beattreat.data.repository.FollowRepository
import com.example.beattreat.data.repository.StorageRepository
import com.example.beattreat.ui.Perfil.PerfilUI
import com.example.beattreat.ui.Perfil.ProfileViewModel
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Sprint 13 — 8 pruebas unitarias con mocks para ProfileViewModel.
 *
 * Constructor real:
 *   ProfileViewModel(
 *     authRepository, storageRepository, firebaseAuth,
 *     firestoreUserRepository, firestoreReviewRepository,
 *     firestoreAlbumRepository, followRepository
 *   )
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val mockAuthRepository          = mockk<AuthRepository>()
    private val mockStorageRepository       = mockk<StorageRepository>()
    private val mockFirebaseAuth            = mockk<FirebaseAuth>()
    private val mockFirestoreUserRepository = mockk<FirestoreUserRepository>()
    private val mockReviewRepository        = mockk<FirestoreReviewRepository>()
    private val mockAlbumRepository         = mockk<FirestoreAlbumRepository>()
    private val mockFollowRepository        = mockk<FollowRepository>()
    private val mockFirebaseUser            = mockk<FirebaseUser>()

    private lateinit var viewModel: ProfileViewModel

    private val fakePerfilUI = PerfilUI(
        id            = 0,
        nombre        = "María Jazz",
        usuario       = "@mariajazz",
        fotoPerfilUrl = "",
        fotoBannerUrl = "",
        siguiendo     = 0,
        seguidores    = 0,
        bio           = "Fanática del jazz"
    )

    /** Mocks necesarios para que el init{} del ViewModel no explote */
    private fun setupDefaultMocks(uid: String = "uid_test") {
        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        every { mockFirebaseUser.uid }         returns uid
        every { mockFirebaseUser.photoUrl }    returns null

        coEvery { mockFirestoreUserRepository.getMyProfile() }    returns Result.success(fakePerfilUI)
        coEvery { mockFollowRepository.getFollowersCount(any()) } returns Result.success(0)
        coEvery { mockFollowRepository.getFollowingCount(any()) } returns Result.success(0)
        coEvery { mockReviewRepository.getReviewsByUser(any()) }  returns Result.success(emptyList())
        coEvery { mockAlbumRepository.getAllAlbumsRaw() }         returns Result.success(emptyMap())
    }

    private fun buildViewModel() = ProfileViewModel(
        authRepository            = mockAuthRepository,
        storageRepository         = mockStorageRepository,
        firebaseAuth              = mockFirebaseAuth,
        firestoreUserRepository   = mockFirestoreUserRepository,
        firestoreReviewRepository = mockReviewRepository,
        firestoreAlbumRepository  = mockAlbumRepository,
        followRepository          = mockFollowRepository
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        setupDefaultMocks()
        viewModel = buildViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Prueba 1: el ViewModel se construye sin explotar ──────────────────────
    @Test
    fun `el viewModel se construye correctamente y el estado no es null`() {
        assertThat(viewModel.uiState.value).isNotNull()
    }

    // ── Prueba 2: errorMessage comienza en null ───────────────────────────────
    @Test
    fun `errorMessage inicial es null`() {
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    // ── Prueba 3: refrescarPerfil carga el nombre del usuario ─────────────────
    @Test
    fun `refrescarPerfil actualiza nombre en el estado`() = runTest {
        viewModel.refrescarPerfil()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.perfil?.nombre).isEqualTo("María Jazz")
    }

    // ── Prueba 4: refrescarPerfil carga el username con @ ────────────────────
    @Test
    fun `refrescarPerfil actualiza usuario con arroba`() = runTest {
        viewModel.refrescarPerfil()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.perfil?.usuario).isEqualTo("@mariajazz")
    }

    // ── Prueba 5: isLoading es false después de completar la carga ────────────
    @Test
    fun `isLoading es false despues de cargar`() = runTest {
        viewModel.refrescarPerfil()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    // ── Prueba 6: seguidores se actualiza con el valor real de Firestore ───────
    @Test
    fun `refrescarPerfil actualiza seguidores con valor de FollowRepository`() = runTest {
        coEvery { mockFollowRepository.getFollowersCount("uid_test") } returns Result.success(99)

        viewModel.refrescarPerfil()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.perfil?.seguidores).isEqualTo(99)
    }

    // ── Prueba 7: siguiendo se actualiza con el valor real de Firestore ────────
    @Test
    fun `refrescarPerfil actualiza siguiendo con valor de FollowRepository`() = runTest {
        coEvery { mockFollowRepository.getFollowingCount("uid_test") } returns Result.success(12)

        viewModel.refrescarPerfil()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.perfil?.siguiendo).isEqualTo(12)
    }

    // ── Prueba 8: sin usuario autenticado el ViewModel no lanza excepción ──────
    @Test
    fun `sin usuario autenticado refrescarPerfil no lanza excepcion`() = runTest {
        every { mockFirebaseAuth.currentUser } returns null

        viewModel.refrescarPerfil()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isNotNull()
    }
}