package com.example.beattreat.viewModels

import com.example.beattreat.data.datasource.AuthRemoteDataSource
import com.example.beattreat.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
import com.example.beattreat.data.repository.AuthRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.example.beattreat.ui.Registro.RegistroViewModel
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 *  - Opción 1: esperar con flow.first { condicion }
 *  - Opción 2: pasar dispatcher + advanceUntilIdle
 *  - Opción 3: llamar directamente suspend functions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelIntegrationTest {

    private lateinit var viewModel: RegistroViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var firestoreUserRepository: FirestoreUserRepository

    @Before
    fun setup() {
        // Conectar a los emuladores (igual que el profe)
        try {
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) { /* ya configurados */ }

        val auth      = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val authDataSource = AuthRemoteDataSource(auth)
        val userDataSource = UserFirestoreDataSourceImpl(firestore)
        firestoreUserRepository = FirestoreUserRepository(userDataSource, auth)
        authRepository = AuthRepository(authDataSource)
    }

    @After
    fun tearDown() = runTest {
        // Limpiar usuario creado durante el test (igual que el profe)
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.await()
        FirebaseAuth.getInstance().signOut()
    }

    // ── Opción 1: esperar con flow.first { } ─────────────────────────────────
    // El profe dice: "esperamos a que el estado cambie porque estamos
    // esperando tiempo real de conexión"

    @Test
    fun register_exitoso_navegaYNoMuestraError() = runTest {
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository)

        viewModel.onEmailChange("beattest@test.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Beat Tester")
        viewModel.onUsernameChange("beattester")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // Opción 1: suspende hasta que registroExitoso sea true
        val registroExitoso = viewModel.uiState
            .map { it.registroExitoso }
            .first { it }

        assertThat(registroExitoso).isTrue()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isTrue()
        assertThat(state.errorMessage).isNull()
    }

    // ── Opción 2: pasar dispatcher + advanceUntilIdle ─────────────────────────
    // El profe dice: "al pasar el dispatcher se deberían ejecutar todas las
    // corrutinas, pero no siempre funciona con Firebase"

    @Test
    fun register_emailYaEnUso_muestraMensajeDeError() = runTest {
        // Primero crear el usuario para que el email quede ocupado
        authRepository.signUp("usado@beattreat.com", "123456")

        viewModel = RegistroViewModel(authRepository, firestoreUserRepository)

        viewModel.onEmailChange("usado@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan Beat")
        viewModel.onUsernameChange("juanbeat")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // Esperar a que loading pase de true a false
        var loading = viewModel.uiState.map { it.isLoading }.first { it }
        assertThat(loading).isTrue()
        loading = viewModel.uiState.map { it.isLoading }.first { !it }
        assertThat(loading).isFalse()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isNotEmpty()
    }

    @Test
    fun register_emailInvalido_muestraError() = runTest {
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository)

        viewModel.onEmailChange("emailsinformato")  // sin @
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // Esperar a que loading pase de true a false
        var loading = viewModel.uiState.map { it.isLoading }.first { it }
        assertThat(loading).isTrue()
        loading = viewModel.uiState.map { it.isLoading }.first { !it }
        assertThat(loading).isFalse()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).contains("válido")
    }

    @Test
    fun register_contrasenaCorta_muestraError() = runTest {
        // La validación de contraseña corta ocurre en el viewModel antes de
        // llamar al repositorio — no requiere emulador
        val testScheduler = TestCoroutineScheduler()
        val dispatcher    = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        viewModel = RegistroViewModel(authRepository, firestoreUserRepository)

        viewModel.onEmailChange("valido@beattreat.com")
        viewModel.onPasswordChange("123") // menos de 6
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
    }

    @Test
    fun register_sinNombre_muestraError() = runTest {
        val testScheduler = TestCoroutineScheduler()
        val dispatcher    = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        viewModel = RegistroViewModel(authRepository, firestoreUserRepository)

        viewModel.onEmailChange("valido@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("") // nombre vacío
        viewModel.onUsernameChange("juanito")

        viewModel.registrar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
    }
}
