package com.example.beattreat.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
 * E2E Caso 1:
 *
 * Un usuario nuevo ingresa a la aplicación. Ingresa al registro y mete todos
 * sus datos. La primera vez escribe como contraseña "1234" (muy corta),
 * por lo cual no se puede registrar. Se verifica el mensaje de error.
 * El usuario corrige la contraseña a "123456" e ingresa a la aplicación.
 * El usuario ingresa a la primera publicación (álbum). Se verifica que la
 * información de detalle sea correcta. Luego va a la sección de reviews y
 * da like al primer comentario. Se verifica que aumente la cantidad de likes.
 * El usuario vuelve atrás, vuelve a seleccionar la publicación, ahora quita
 * el like y se verifica que la cantidad de likes disminuya.
 */
@HiltAndroidTest
class RegisterNewUserE2ECompleto {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var authRepository: AuthRepository
    private lateinit var firestoreUserRepository: FirestoreUserRepository

    private val testEmail = "e2e_nuevo_${System.currentTimeMillis()}@beattreat.com"

    @Before
    fun setup() {
        hiltRule.inject()

        try {
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) { }

        // Conceder permiso de notificaciones para evitar popups
        androidx.test.platform.app.InstrumentationRegistry
            .getInstrumentation()
            .uiAutomation
            .executeShellCommand(
                "pm grant com.example.beattreat android.permission.POST_NOTIFICATIONS"
            )

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val authDataSource = AuthRemoteDataSource(auth)
        val userDataSource = UserFirestoreDataSourceImpl(firestore)

        firestoreUserRepository = FirestoreUserRepository(userDataSource, auth)
        authRepository = AuthRepository(authDataSource)
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun navigate_fromStart_toLaunchScreen() {
        composeRule.onNodeWithTag("loginScreen").assertIsDisplayed()
    }

    @Test
    fun navigate_toRegistro_clickEnRegistro() {
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
        composeRule.onNodeWithTag("emailField").performTextInput(testEmail)
        composeRule.onNodeWithTag("passwordField").performTextInput("1234") // corta

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        composeRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    }

    @Test
    fun register_emailYaRegistrado_muestraMensajeDeError() {
        // Crear el usuario base primero
        runBlocking { authRepository.signUp("admin_e2e@beattreat.com", "123456") }

        composeRule.onNodeWithTag("btnRegistro").performClick()

        composeRule.onNodeWithTag("nombreField").performTextInput("Admin Beat")
        composeRule.onNodeWithTag("usuarioField").performTextInput("adminbeat")
        composeRule.onNodeWithTag("paisField").performTextInput("Colombia")
        composeRule.onNodeWithTag("bioField").performTextInput("Administrador")
        composeRule.onNodeWithTag("emailField").performTextInput("admin_e2e@beattreat.com")
        composeRule.onNodeWithTag("passwordField").performTextInput("123456")

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag("errorMessage")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    }

    @Test
    fun register_todosLosCamposValidos_navegaAHome() {
        composeRule.onNodeWithTag("btnRegistro").performClick()

        composeRule.onNodeWithTag("nombreField").performTextInput("Nuevo Beat Fan")
        composeRule.onNodeWithTag("usuarioField").performTextInput("nuevobeatfan_${System.currentTimeMillis()}")
        composeRule.onNodeWithTag("paisField").performTextInput("México")
        composeRule.onNodeWithTag("bioField").performTextInput("Fan del rock y reggaetón")
        composeRule.onNodeWithTag("emailField").performTextInput(testEmail)
        composeRule.onNodeWithTag("passwordField").performTextInput("123456")

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        composeRule.waitUntil(timeoutMillis = 15000) {
            composeRule.onAllNodesWithTag("homeScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("homeScreen").assertIsDisplayed()
    }

    /**
     * Flujo completo E2E:
     *
     * 1. Ir al registro
     * 2. Llenar datos con contraseña "1234" → verificar error
     * 3. Corregir contraseña a "123456" → registrar → ir al home
     * 4. Navegar a la primera publicación (álbum)
     * 5. Verificar que se muestra la información del álbum
     * 6. Ir a la sección de reviews
     * 7. Dar like al primer comentario → verificar que aumentó
     * 8. Volver al álbum → volver al home → entrar al álbum de nuevo
     * 9. Ir a reviews → quitar like → verificar que disminuyó
     */
    @Test
    fun flujoCompleto_registro_publicacion_like_unlike() {
        // ── Paso 1: ir al registro ────────────────────────────────────────────
        composeRule.onNodeWithTag("btnRegistro").performClick()
        composeRule.onNodeWithTag("registroScreen").assertIsDisplayed()

        val email = "flujo_completo_${System.currentTimeMillis()}@beattreat.com"

        // ── Paso 2: llenar con contraseña corta → verificar error ─────────────
        composeRule.onNodeWithTag("nombreField").performTextInput("Beat E2E")
        composeRule.onNodeWithTag("usuarioField").performTextInput("beate2e_${System.currentTimeMillis()}")
        composeRule.onNodeWithTag("paisField").performTextInput("Colombia")
        composeRule.onNodeWithTag("bioField").performTextInput("Usuario de prueba E2E")
        composeRule.onNodeWithTag("emailField").performTextInput(email)
        composeRule.onNodeWithTag("passwordField").performTextInput("1234")

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        // Verificar que aparece el error de contraseña corta
        composeRule.onNodeWithTag("errorMessage").assertIsDisplayed()

        // ── Paso 3: corregir contraseña y registrar ───────────────────────────
        // Limpiar el campo de contraseña y poner la correcta
        composeRule.onNodeWithTag("passwordField").performTextClearance()
        composeRule.onNodeWithTag("passwordField").performTextInput("123456")

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        // Esperar a que navegue al home
        composeRule.waitUntil(timeoutMillis = 15000) {
            composeRule.onAllNodesWithTag("homeScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("homeScreen").assertIsDisplayed()

        // ── Paso 4: navegar al primer álbum ──────────────────────────────────
        // Hacer click en el primer álbum de la lista
        composeRule.onAllNodesWithTag("albumItem")
            .onFirst()
            .performClick()

        // Esperar a que cargue la pantalla de detalle
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag("albumDetalleScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // ── Paso 5: verificar que se muestra la información del álbum ─────────
        // El nombre del álbum debe estar visible
        composeRule.onNodeWithTag("albumNombre").assertIsDisplayed()
        // El artista debe estar visible
        composeRule.onNodeWithTag("albumArtista").assertIsDisplayed()

        // ── Paso 6: ir a la sección de reviews ───────────────────────────────
        composeRule.onNodeWithTag("btnVerResenas").performClick()

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag("resenaScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Esperar a que carguen las reviews
        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("resenaCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // ── Paso 7: dar like al primer comentario y verificar aumento ─────────
        // Leer el contador de likes antes del like
        val likesAntes = composeRule
            .onAllNodesWithTag("likesCount")
            .onFirst()
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
            .firstOrNull()?.text?.toIntOrNull() ?: 0

        // Dar like
        composeRule.onAllNodesWithTag("btnLike")
            .onFirst()
            .performClick()

        // Esperar a que se actualice el contador
        composeRule.waitUntil(timeoutMillis = 5000) {
            val likesActual = composeRule
                .onAllNodesWithTag("likesCount")
                .onFirst()
                .fetchSemanticsNode()
                .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
            likesActual > likesAntes
        }

        // Verificar que el contador aumentó
        val likesDespues = composeRule
            .onAllNodesWithTag("likesCount")
            .onFirst()
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
            .firstOrNull()?.text?.toIntOrNull() ?: 0

        assert(likesDespues == likesAntes + 1) {
            "Se esperaba ${likesAntes + 1} likes, pero había $likesDespues"
        }

        // ── Paso 8: volver y entrar de nuevo al mismo álbum ──────────────────
        composeRule.onNodeWithTag("btnBack").performClick()  // salir de reviews
        composeRule.onNodeWithTag("btnBack").performClick()  // salir del detalle

        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("homeScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Volver a entrar al mismo álbum
        composeRule.onAllNodesWithTag("albumItem")
            .onFirst()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag("albumDetalleScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Ir a reviews de nuevo
        composeRule.onNodeWithTag("btnVerResenas").performClick()

        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("resenaCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // ── Paso 9: quitar el like y verificar que disminuye ─────────────────
        val likesAntesUnlike = composeRule
            .onAllNodesWithTag("likesCount")
            .onFirst()
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
            .firstOrNull()?.text?.toIntOrNull() ?: 0

        // Quitar el like (misma acción toggle)
        composeRule.onAllNodesWithTag("btnLike")
            .onFirst()
            .performClick()

        // Esperar a que se actualice el contador
        composeRule.waitUntil(timeoutMillis = 5000) {
            val likesActual = composeRule
                .onAllNodesWithTag("likesCount")
                .onFirst()
                .fetchSemanticsNode()
                .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
            likesActual < likesAntesUnlike
        }

        val likesFinal = composeRule
            .onAllNodesWithTag("likesCount")
            .onFirst()
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
            .firstOrNull()?.text?.toIntOrNull() ?: 0

        assert(likesFinal == likesAntesUnlike - 1) {
            "Se esperaba ${likesAntesUnlike - 1} likes tras quitar like, pero había $likesFinal"
        }
    }

    @After
    fun cleanDatabase() = runTest {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.await()
        FirebaseAuth.getInstance().signOut()
    }
}