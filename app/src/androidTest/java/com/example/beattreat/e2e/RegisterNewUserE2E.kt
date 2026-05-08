package com.example.beattreat.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.beattreat.MainActivity
import com.example.beattreat.data.datasource.AuthRemoteDataSource
import com.example.beattreat.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
import com.example.beattreat.data.repository.AuthRepository
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Sprint 12 — Prueba E2E de registro de nuevo usuario.
 *
 * Igual que el profesor: usa HiltAndroidRule + createAndroidComposeRule
 * con testTags en la UI para interactuar con ella.
 *
 * REQUISITO: los Composables deben tener los Modifier.testTag correspondientes.
 * Ver sección "testTags requeridos" al final de este archivo.
 */
@HiltAndroidTest
class RegisterNewUserE2E {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var authRepository: AuthRepository
    private lateinit var firestoreUserRepository: FirestoreUserRepository

    @Before
    fun setup() {
        hiltRule.inject()

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

        // Crear usuario admin para tests de email ya registrado
        runBlocking {
            try {
                authRepository.signUp("admin@beattreat.com", "123456")
                authRepository.signOut()
            } catch (e: Exception) { /* el usuario ya existe, ok */ }
        }
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun navigate_fromStart_toLaunchScreen() {
        // La app abre en la pantalla de login o perfil según si hay sesión
        composeRule.onNodeWithTag("loginScreen")
            .assertIsDisplayed()
    }

    @Test
    fun navigate_toRegistro_clickEnRegistro() {
        // Desde la pantalla de login, navega al registro
        composeRule.onNodeWithTag("btnRegistro").performClick()
        composeRule.onNodeWithTag("registroScreen").assertIsDisplayed()
    }

    @Test
    fun register_contrasenaCorta_muestraMensajeDeError() {
        composeRule.onNodeWithTag("btnRegistro").performClick()

        composeRule.onNodeWithTag("nombreField").performTextInput("Juan Beat")
        composeRule.onNodeWithTag("usuarioField").performTextInput("juanbeat")
        composeRule.onNodeWithTag("paisField").performTextInput("Colombia")
        composeRule.onNodeWithTag("bioField").performTextInput("Fan de la música")
        composeRule.onNodeWithTag("emailField").performTextInput("juan@beattreat.com")
        composeRule.onNodeWithTag("passwordField").performTextInput("123") // corta

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        composeRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    }

    @Test
    fun register_emailYaRegistrado_muestraMensajeDeError() {
        composeRule.onNodeWithTag("btnRegistro").performClick()

        composeRule.onNodeWithTag("nombreField").performTextInput("Admin Beat")
        composeRule.onNodeWithTag("usuarioField").performTextInput("adminbeat")
        composeRule.onNodeWithTag("paisField").performTextInput("Colombia")
        composeRule.onNodeWithTag("bioField").performTextInput("Administrador")
        composeRule.onNodeWithTag("emailField").performTextInput("admin@beattreat.com") // ya existe
        composeRule.onNodeWithTag("passwordField").performTextInput("123456")

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        composeRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    }

    @Test
    fun register_todosLosCamposValidos_navegaAHome() {
        composeRule.onNodeWithTag("btnRegistro").performClick()

        composeRule.onNodeWithTag("nombreField").performTextInput("Nuevo Beat Fan")
        composeRule.onNodeWithTag("usuarioField").performTextInput("nuevobeatfan")
        composeRule.onNodeWithTag("paisField").performTextInput("México")
        composeRule.onNodeWithTag("bioField").performTextInput("Fan del rock y reggaetón")
        composeRule.onNodeWithTag("emailField").performTextInput("nuevo${System.currentTimeMillis()}@beattreat.com")
        composeRule.onNodeWithTag("passwordField").performTextInput("123456")

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        // Esperar a que navegue al home (igual que el profe)
        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("homeScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("homeScreen").assertIsDisplayed()
    }

    @After
    fun cleanDatabase() = runTest {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.await()
        FirebaseAuth.getInstance().signOut()
    }
}

/*
 * ── testTags requeridos en la UI ─────────────────────────────────────────────
 *
 * Para que los tests E2E funcionen, agrega estos Modifier.testTag()
 * en los Composables de Login y Registro:
 *
 * LoginScreen.kt:
 *   - La pantalla raíz:      Modifier.testTag("loginScreen")
 *   - Botón ir a registro:   Modifier.testTag("btnRegistro")
 *
 * RegistroScreen.kt:
 *   - La pantalla raíz:      Modifier.testTag("registroScreen")
 *   - Campo nombre:          Modifier.testTag("nombreField")
 *   - Campo username:        Modifier.testTag("usuarioField")
 *   - Campo país:            Modifier.testTag("paisField")
 *   - Campo bio:             Modifier.testTag("bioField")
 *   - Campo email:           Modifier.testTag("emailField")
 *   - Campo password:        Modifier.testTag("passwordField")
 *   - Botón registrar:       Modifier.testTag("btnRegistrar")
 *   - Texto de error:        Modifier.testTag("errorMessage")
 *
 * HomeScreen.kt:
 *   - La pantalla raíz:      Modifier.testTag("homeScreen")
 * ─────────────────────────────────────────────────────────────────────────────
 */
