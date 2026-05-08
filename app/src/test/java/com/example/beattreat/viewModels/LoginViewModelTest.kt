package com.example.beattreat.viewModels

import com.example.beattreat.data.repository.AuthRepository
import com.example.beattreat.ui.Login.LoginViewModel
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
import kotlin.test.assertNotEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun `cuando onEmailChange es llamado, el estado se actualiza con el nuevo email`() {
        viewModel.onEmailChange("beatfan@email.com")

        assertThat(viewModel.uiState.value.email).isEqualTo("beatfan@email.com")
    }

    @Test
    fun `cuando onPasswordChange es llamado, el estado se actualiza con la nueva contrasena`() {
        viewModel.onPasswordChange("mipassword123")

        assertThat(viewModel.uiState.value.password).isEqualTo("mipassword123")
    }

    @Test
    fun `estado inicial del viewModel tiene email y password vacios`() {
        val state = viewModel.uiState.value

        assertThat(state.email).isEmpty()
        assertThat(state.password).isEmpty()
        assertThat(state.loginExitoso).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `cuando email y password estan vacios, login muestra error`() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.loginExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isNotEmpty()
    }

    @Test
    fun `cuando repo devuelve success, el estado loginExitoso es true`() = runTest {
        coEvery {
            authRepository.signIn("beatfan@email.com", "123456")
        } returns Result.success(Unit)

        viewModel.onEmailChange("beatfan@email.com")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.loginExitoso).isTrue()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `cuando repo devuelve failure, el estado muestra el mensaje de error`() = runTest {
        coEvery {
            authRepository.signIn(any(), any())
        } returns Result.failure(Exception("Correo o contraseña incorrectos"))

        viewModel.onEmailChange("wrong@email.com")
        viewModel.onPasswordChange("wrongpass")
        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.loginExitoso).isFalse()
        assertThat(state.errorMessage).isEqualTo("Correo o contraseña incorrectos")
    }

    @Test
    fun `resetLoginExitoso vuelve loginExitoso a false`() = runTest {
        coEvery {
            authRepository.signIn(any(), any())
        } returns Result.success(Unit)

        viewModel.onEmailChange("beatfan@email.com")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.loginExitoso).isTrue()

        viewModel.resetLoginExitoso()

        assertThat(viewModel.uiState.value.loginExitoso).isFalse()
    }

    @Test
    fun `cuando credenciales son invalidas, loginExitoso sigue siendo false`() = runTest {
        coEvery {
            authRepository.signIn(any(), any())
        } returns Result.failure(Exception("No existe una cuenta con ese correo"))

        viewModel.onEmailChange("noexiste@email.com")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.loginExitoso).isFalse()
        assertThat(viewModel.uiState.value.errorMessage).isNotNull()
    }

    @Test
    fun `cuando solo el email esta vacio, el login no navega`() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.loginExitoso).isFalse()
    }

    @Test
    fun `cuando solo el password esta vacio, el login no navega`() = runTest {
        viewModel.onEmailChange("beatfan@email.com")
        viewModel.onPasswordChange("")
        viewModel.login()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.loginExitoso).isFalse()
    }
}