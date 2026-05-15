package com.example.beattreat.viewModels

import com.example.beattreat.data.datasource.AuthRemoteDataSource
import com.example.beattreat.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
import com.example.beattreat.data.repository.AuthRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.example.beattreat.ui.Registro.RegistroViewModel
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Sprint 12 — Pruebas de integración para RegistroViewModel.
 * CORREGIDO Sprint 13: se agrega el parámetro 'dispatcher' al constructor.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelIntegrationTest {

    private lateinit var viewModel: RegistroViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var firestoreUserRepository: FirestoreUserRepository

    @Before
    fun setup() {
        try {
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) { /* ya configurados */ }

        val auth      = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val authDataSource = AuthRemoteDataSource(auth)
        val userDataSource = UserFirestoreDataSourceImpl(firestore)
        firestoreUserRepository = FirestoreUserRepository(userDataSource, auth)
        authRepository          = AuthRepository(authDataSource)
    }

    @After
    fun tearDown() = runTest {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.await()
        FirebaseAuth.getInstance().signOut()
    }

    @Test
    fun register_exitoso_navegaYNoMuestraError() = runTest {
        viewModel = RegistroViewModel(
            authRepository          = authRepository,
            firestoreUserRepository = firestoreUserRepository,
            dispatcher              = Dispatchers.IO   // ← FIX
        )

        viewModel.onEmailChange("beattest@test.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Beat Tester")
        viewModel.onUsernameChange("beattester")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        val registroExitoso = viewModel.uiState
            .map { it.registroExitoso }
            .first { it }

        assertThat(registroExitoso).isTrue()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isTrue()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun register_emailYaEnUso_muestraMensajeDeError() = runTest {
        authRepository.signUp("usado@beattreat.com", "123456")

        viewModel = RegistroViewModel(
            authRepository          = authRepository,
            firestoreUserRepository = firestoreUserRepository,
            dispatcher              = Dispatchers.IO   // ← FIX
        )

        viewModel.onEmailChange("usado@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan Beat")
        viewModel.onUsernameChange("juanbeat")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

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
        viewModel = RegistroViewModel(
            authRepository          = authRepository,
            firestoreUserRepository = firestoreUserRepository,
            dispatcher              = Dispatchers.IO   // ← FIX
        )

        viewModel.onEmailChange("emailsinformato")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

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
        viewModel = RegistroViewModel(
            authRepository          = authRepository,
            firestoreUserRepository = firestoreUserRepository,
            dispatcher              = Dispatchers.IO   // ← FIX
        )

        viewModel.onEmailChange("valido@beattreat.com")
        viewModel.onPasswordChange("123")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // Validación local — no hay loading, resultado inmediato
        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
    }

    @Test
    fun register_sinNombre_muestraError() = runTest {
        viewModel = RegistroViewModel(
            authRepository          = authRepository,
            firestoreUserRepository = firestoreUserRepository,
            dispatcher              = Dispatchers.IO   // ← FIX
        )

        viewModel.onEmailChange("valido@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("")
        viewModel.onUsernameChange("juanito")

        viewModel.registrar()

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
    }
}