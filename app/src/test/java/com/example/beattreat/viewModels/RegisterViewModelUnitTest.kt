package com.example.beattreat.viewModels

import com.example.beattreat.data.repository.AuthRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.example.beattreat.ui.Registro.RegistroViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelUnitTest {

    private lateinit var viewModel: RegistroViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var firestoreUserRepository: FirestoreUserRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        firestoreUserRepository = mockk()
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun `registro exitoso, navega y no muestra error`() = runTest {
        // arrange
        viewModel.onEmailChange("nuevo@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan Beat")
        viewModel.onUsernameChange("juanbeat")
        viewModel.onCountryChange("Colombia")
        viewModel.onBioChange("Fan de la música")

        coEvery {
            authRepository.signUp(any(), any())
        } returns Result.success(Unit)

        coEvery {
            firestoreUserRepository.registerUser(any(), any(), any(), any())
        } returns Result.success(Unit)

        viewModel.registrar()
        advanceUntilIdle()

        // assert
        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isTrue()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `registro con email ya en uso, muestra mensaje de error`() = runTest {
        // arrange
        viewModel.onEmailChange("usado@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito")
        viewModel.onCountryChange("Colombia")

        coEvery {
            authRepository.signUp(any(), any())
        } returns Result.failure(Exception("Este correo ya está registrado"))

        viewModel.registrar()
        advanceUntilIdle()

        // assert
        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isEqualTo("Este correo ya está registrado")
    }

    @Test
    fun `registro con email invalido, muestra error`() = runTest {
        // arrange — email mal formado
        viewModel.onEmailChange("emailsinforma")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito")
        viewModel.onCountryChange("Colombia")

        coEvery {
            authRepository.signUp(any(), any())
        } returns Result.failure(Exception("El formato del correo no es válido"))

        viewModel.registrar()
        advanceUntilIdle()

        // assert
        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isEqualTo("El formato del correo no es válido")
    }

    @Test
    fun `registro sin nombre, no llama al repositorio`() = runTest {
        // El viewModel valida que nombre no esté vacío antes de llamar al repo
        viewModel.onEmailChange("test@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("") // nombre vacío
        viewModel.onUsernameChange("juanito")

        viewModel.registrar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
    }

    @Test
    fun `registro sin username, no llama al repositorio`() = runTest {
        viewModel.onEmailChange("test@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("") // username vacío

        viewModel.registrar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
    }

    @Test
    fun `registro con contrasena corta, muestra error`() = runTest {
        viewModel.onEmailChange("test@beattreat.com")
        viewModel.onPasswordChange("123") // menos de 6 chars
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito")

        viewModel.registrar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
    }

    @Test
    fun `onEmailChange actualiza correctamente el estado`() {
        viewModel.onEmailChange("fan@beattreat.com")

        assertThat(viewModel.uiState.value.email).isEqualTo("fan@beattreat.com")
    }

    @Test
    fun `estado inicial de registroExitoso es false`() {
        assertThat(viewModel.uiState.value.registroExitoso).isFalse()
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }
}
