package com.example.beattreat.viewModels

import com.example.beattreat.data.datasource.AuthRemoteDataSource
import com.example.beattreat.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
import com.example.beattreat.data.repository.AuthRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.example.beattreat.ui.Registro.RegistroViewModel
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Tests de integración para RegistroViewModel
 * NOTA: Estos tests requieren que los emuladores de Firebase estén corriendo
 */
class RegisterViewModelIntegrationTest {

    private lateinit var viewModel: RegistroViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var firestoreUserRepository: FirestoreUserRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Conectar a los emuladores
        try {
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) {
            println("Emuladores ya configurados o error: ${e.message}")
        }

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val authDataSource = AuthRemoteDataSource(auth)
        val userDataSource = UserFirestoreDataSourceImpl(firestore)
        firestoreUserRepository = FirestoreUserRepository(userDataSource, auth)
        authRepository = AuthRepository(authDataSource)
    }

    @After
    fun tearDown() {
        runBlocking {
            try {
                val user = FirebaseAuth.getInstance().currentUser
                user?.delete()?.await()
            } catch (e: Exception) {
                println("Error al eliminar usuario: ${e.message}")
            }
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                println("Error al cerrar sesión: ${e.message}")
            }
        }
    }

    @Test
    fun register_exitoso_navegaYNoMuestraError(): Unit = runBlocking {
        val email = "beattest_${System.currentTimeMillis()}@test.com"
        val username = "beattester_${System.currentTimeMillis()}"

        viewModel = RegistroViewModel(authRepository, firestoreUserRepository, testDispatcher)

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Beat Tester")
        viewModel.onUsernameChange(username)
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // Esperar a que el estado cambie (máximo 10 segundos)
        var attempts = 0
        var registroExitoso = false
        while (attempts < 50 && !registroExitoso) {
            delay(200)
            registroExitoso = viewModel.uiState.value.registroExitoso
            attempts++
        }

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isTrue()
        assertThat(state.errorMessage).isNull()

        // Limpiar el usuario creado
        try {
            FirebaseAuth.getInstance().currentUser?.delete()?.await()
        } catch (e: Exception) {
            println("Error al limpiar usuario: ${e.message}")
        }
    }

    @Test
    fun register_emailYaEnUso_muestraMensajeDeError(): Unit = runBlocking {
        val emailEnUso = "usado_${System.currentTimeMillis()}@beattreat.com"

        // Primero crear el usuario para que el email quede ocupado
        val signUpResult = authRepository.signUp(emailEnUso, "123456")
        assertThat(signUpResult.isSuccess).isTrue()

        // IMPORTANTE: Firebase Auth necesita tiempo para procesar el usuario
        delay(1000)

        // Guardar referencia al usuario creado para limpiarlo después
        val userCreado = FirebaseAuth.getInstance().currentUser

        viewModel = RegistroViewModel(authRepository, firestoreUserRepository, testDispatcher)

        viewModel.onEmailChange(emailEnUso)  // Mismo email que ya está registrado
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan Beat")
        viewModel.onUsernameChange("juanbeat_${System.currentTimeMillis()}")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // Esperar a que se complete la operación
        var attempts = 0
        var isLoading = true
        while (attempts < 50 && isLoading) {
            delay(200)
            isLoading = viewModel.uiState.value.isLoading
            attempts++
        }

        // Dar tiempo extra para que Firebase procese el error
        delay(1000)

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()

        // Verificar que el mensaje de error indique que el email ya está en uso
        val errorMessage = state.errorMessage ?: ""
        // Firebase puede devolver varios mensajes posibles
        val esErrorEmailEnUso = errorMessage.contains("email", ignoreCase = true) &&
                (errorMessage.contains("already", ignoreCase = true) ||
                        errorMessage.contains("exist", ignoreCase = true) ||
                        errorMessage.contains("registrado", ignoreCase = true) ||
                        errorMessage.contains("use", ignoreCase = true))

        // Si la condición anterior falla, mostrar el mensaje real para debug
        if (!esErrorEmailEnUso) {
            println("Mensaje de error recibido: '$errorMessage'")
        }

        assertThat(esErrorEmailEnUso).isTrue()

        // Limpiar: eliminar el usuario creado
        try {
            userCreado?.delete()?.await()
        } catch (e: Exception) {
            println("Error al limpiar usuario: ${e.message}")
        }
    }

    @Test
    fun register_emailInvalido_muestraError() = runBlocking {
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository, testDispatcher)

        viewModel.onEmailChange("emailsinformato")  // sin @
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito_${System.currentTimeMillis()}")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        // Esperar a que se complete la operación
        var attempts = 0
        var isLoading = true
        while (attempts < 50 && isLoading) {
            delay(200)
            isLoading = viewModel.uiState.value.isLoading
            attempts++
        }

        delay(1000)

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        val errorMessage = state.errorMessage ?: ""

        // Verificar que el mensaje indique email inválido
        val esEmailInvalido = errorMessage.contains("email", ignoreCase = true) &&
                (errorMessage.contains("invalid", ignoreCase = true) ||
                        errorMessage.contains("formato", ignoreCase = true) ||
                        errorMessage.contains("badly formatted", ignoreCase = true))

        if (!esEmailInvalido) {
            println("Mensaje de error recibido para email inválido: '$errorMessage'")
        }

        assertThat(esEmailInvalido).isTrue()
    }

    @Test
    fun register_contrasenaCorta_muestraError() = runBlocking {
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository, testDispatcher)

        viewModel.onEmailChange("valido@beattreat.com")
        viewModel.onPasswordChange("123") // menos de 6
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("juanito_${System.currentTimeMillis()}")
        viewModel.onCountryChange("Colombia")

        viewModel.registrar()

        delay(100)

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isEqualTo("La contraseña debe tener al menos 6 caracteres")
    }

    @Test
    fun register_sinNombre_muestraError() = runBlocking {
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository, testDispatcher)

        viewModel.onEmailChange("valido@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("") // nombre vacío
        viewModel.onUsernameChange("juanito_${System.currentTimeMillis()}")

        viewModel.registrar()

        delay(100)

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isEqualTo("Ingresa tu nombre")
    }

    @Test
    fun register_sinUsername_muestraError() = runBlocking {
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository, testDispatcher)

        viewModel.onEmailChange("valido@beattreat.com")
        viewModel.onPasswordChange("123456")
        viewModel.onNombreChange("Juan")
        viewModel.onUsernameChange("") // username vacío

        viewModel.registrar()

        delay(100)

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isEqualTo("Ingresa un nombre de usuario")
    }

    @Test
    fun register_camposVacios_muestraError() = runBlocking {
        viewModel = RegistroViewModel(authRepository, firestoreUserRepository, testDispatcher)

        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.onNombreChange("")
        viewModel.onUsernameChange("")

        viewModel.registrar()

        delay(100)

        val state = viewModel.uiState.value
        assertThat(state.registroExitoso).isFalse()
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.errorMessage).isEqualTo("Completa email y contraseña")
    }
}