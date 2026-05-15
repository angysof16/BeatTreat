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
 * Sprint 13 — 2 pruebas de integración para RegistroViewModel.
 *
 * IMPORTANTE — estas pruebas van en:
 *   app/src/androidTest/java/com/example/beattreat/viewModels/
 *
 * Requieren los emuladores de Firebase corriendo:
 *   firebase emulators:start --only auth,firestore
 *   - Auth     → 10.0.2.2:9099
 *   - Firestore → 10.0.2.2:8080
 *
 * El RegistroViewModel recibe un @IoDispatcher (CoroutineDispatcher).
 * En el test pasamos Dispatchers.IO directamente — es válido para pruebas
 * de integración porque NO usamos TestCoroutineScheduler; la corrutina corre
 * en tiempo real contra el emulador y usamos flow.first{} para esperar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelIntegrationTest {

    private lateinit var viewModel: RegistroViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var firestoreUserRepository: FirestoreUserRepository

    // Email único para evitar colisiones entre ejecuciones
    private val testEmail = "sprint13_integ_${System.currentTimeMillis()}@beattreat.com"

    @Before
    fun setup() {
        // Conectar a los emuladores (seguro llamar varias veces)
        try { FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099) } catch (_: Exception) { }
        try { FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080) } catch (_: Exception) { }

        val auth      = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val authDataSource = AuthRemoteDataSource(auth)
        val userDataSource = UserFirestoreDataSourceImpl(firestore)

        firestoreUserRepository = FirestoreUserRepository(userDataSource, auth)
        authRepository          = AuthRepository(authDataSource)
    }

    @After
    fun tearDown() = runTest {
        // Limpiar usuario creado durante el test
        try { FirebaseAuth.getInstance().currentUser?.delete()?.await() } catch (_: Exception) { }
        FirebaseAuth.getInstance().signOut()
    }

    // ── Integración 1 ─────────────────────────────────────────────────────────
    /**
     * Registro exitoso contra el emulador:
     *  - Todos los campos válidos.
     *  - Espera con flow.first { } a que registroExitoso sea true.
     *  - Verifica que errorMessage sea null.
     *
     * Por qué funciona con Dispatchers.IO:
     *   La corrutina del ViewModel corre en IO real, y flow.first { } suspende
     *   el test hasta recibir el valor esperado (o hasta que el timeout del
     *   runTest lo cancele).
     */
    @Test
    fun registro_exitoso_contra_emulador_registroExitosoEsTrue() = runTest {
        // Crear el ViewModel con Dispatchers.IO (igual que en producción)
        viewModel = RegistroViewModel(
            authRepository          = authRepository,
            firestoreUserRepository = firestoreUserRepository,
            dispatcher              = Dispatchers.IO
        )

        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Sprint Trece Integ")
        viewModel.onUsernameChange("s13integ_${System.currentTimeMillis()}")
        viewModel.onCountryChange("Colombia")
        viewModel.onBioChange("Prueba de integración Sprint 13")

        viewModel.registrar()

        // Suspende hasta que registroExitoso sea true (red real contra emulador)
        val exitoso = viewModel.uiState
            .map { it.registroExitoso }
            .first { it }

        assertThat(exitoso).isTrue()
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    // ── Integración 2 ─────────────────────────────────────────────────────────
    /**
     * Contraseña de 3 caracteres → validación LOCAL en el ViewModel
     * (antes de llamar al emulador) → errorMessage no es null.
     *
     * Esta prueba verifica que la validación del ViewModel funciona
     * correctamente integrada con los repositorios reales.
     * No hay latencia de red porque el ViewModel retorna antes de lanzar
     * la corrutina.
     */
    @Test
    fun registro_contrasenaCorta_muestraErrorSinLlamarAlEmulador() = runTest {
        viewModel = RegistroViewModel(
            authRepository          = authRepository,
            firestoreUserRepository = firestoreUserRepository,
            dispatcher              = Dispatchers.IO
        )

        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange("123")      // menos de 6 caracteres
        viewModel.onNombreChange("Sprint Trece")
        viewModel.onUsernameChange("s13integ")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // La validación es síncrona (return antes del launch), el estado
        // se actualiza antes de que podamos observar el flow
        val state = viewModel.uiState.value

        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.isLoading).isFalse()
    }
}