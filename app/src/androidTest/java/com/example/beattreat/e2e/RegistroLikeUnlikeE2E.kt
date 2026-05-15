package com.example.beattreat.e2e

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.beattreat.MainActivity
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
 * Sprint 13 — E2E Caso 1 (completo):
 *
 * Un usuario nuevo ingresa a la aplicación. Ingresa al registro y mete
 * todos sus datos. La primera vez escribe como contraseña "1234" (muy corta),
 * por lo cual no se puede registrar. Se verifica el mensaje de error.
 * El usuario corrige la contraseña a "123456" e ingresa a la aplicación.
 * El usuario ingresa a la primera publicación (álbum). Se verifica que la
 * información de detalle sea correcta. Luego va a la sección de reviews y
 * da like al primer comentario. Se verifica que aumente la cantidad de likes.
 * El usuario vuelve atrás, vuelve a seleccionar la publicación, ahora quita
 * el like y se verifica que la cantidad de likes disminuya.
 */
@HiltAndroidTest
class RegistroLikeUnlikeE2E {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val testEmail = "e2e_s13_${System.currentTimeMillis()}@beattreat.com"

    @Before
    fun setup() {
        hiltRule.inject()

        try { FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099) } catch (e: Exception) { }
        try { FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080) } catch (e: Exception) { }

        // Conceder permiso de notificaciones para evitar popups en la prueba
        try {
            androidx.test.platform.app.InstrumentationRegistry
                .getInstrumentation().uiAutomation
                .executeShellCommand(
                    "pm grant com.example.beattreat android.permission.POST_NOTIFICATIONS"
                )
        } catch (e: Exception) { }
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Smoke test: la pantalla de login se muestra al abrir la app.
     */
    @Test
    fun app_muestra_pantallaDeLogin_al_iniciar() {
        composeRule.onNodeWithTag("loginScreen").assertIsDisplayed()
    }

    /**
     * Flujo completo E2E:
     *  1. Ir al registro.
     *  2. Llenar datos con contraseña corta → verificar error.
     *  3. Corregir contraseña → registrarse → llegar al home.
     *  4. Entrar al primer álbum → verificar nombre e artista.
     *  5. Ir a reviews → dar like → verificar que aumentó.
     *  6. Volver → reentrar → quitar like → verificar que disminuyó.
     */
    @Test
    fun flujoCompleto_registro_detalleAlbum_like_unlike() {

        // ── Paso 1: ir al registro ────────────────────────────────────────────
        composeRule.onNodeWithTag("loginScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("btnRegistro").performClick()
        composeRule.onNodeWithTag("registroScreen").assertIsDisplayed()

        val username = "s13user_${System.currentTimeMillis()}"

        // ── Paso 2: llenar con contraseña corta → verificar error ─────────────
        composeRule.onNodeWithTag("nombreField").performTextInput("Sprint Trece")
        composeRule.onNodeWithTag("usuarioField").performTextInput(username)
        composeRule.onNodeWithTag("paisField").performTextInput("Colombia")
        composeRule.onNodeWithTag("bioField").performTextInput("E2E Sprint 13")
        composeRule.onNodeWithTag("emailField").performTextInput(testEmail)
        composeRule.onNodeWithTag("passwordField").performTextInput("1234") // contraseña corta

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        // El ViewModel valida localmente → el error es inmediato
        composeRule.onNodeWithTag("errorMessage").assertIsDisplayed()

        // ── Paso 3: corregir contraseña → registrar ───────────────────────────
        composeRule.onNodeWithTag("passwordField").performTextClearance()
        composeRule.onNodeWithTag("passwordField").performTextInput("123456")

        composeRule.onNodeWithTag("btnRegistrar").performClick()

        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithTag("homeScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("homeScreen").assertIsDisplayed()

        // ── Paso 4: abrir el primer álbum ─────────────────────────────────────
        composeRule.onAllNodesWithTag("albumItem")
            .onFirst()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 12000) {
            composeRule.onAllNodesWithTag("albumDetalleScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que se muestra la información del álbum
        composeRule.onNodeWithTag("albumNombre").assertIsDisplayed()
        composeRule.onNodeWithTag("albumArtista").assertIsDisplayed()

        // ── Paso 5: ir a reviews → dar like → verificar aumento ───────────────
        composeRule.onNodeWithTag("btnVerResenas").performClick()

        composeRule.waitUntil(timeoutMillis = 12000) {
            composeRule.onAllNodesWithTag("resenaScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Esperar a que haya al menos una reseña cargada
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag("resenaCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Leer likes antes del like
        val likesAntes = runCatching {
            composeRule.onAllNodesWithTag("likesCount")
                .onFirst()
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        // Dar like
        composeRule.onAllNodesWithTag("btnLike")
            .onFirst()
            .performClick()

        // Esperar a que el contador aumente
        composeRule.waitUntil(timeoutMillis = 8000) {
            val actual = runCatching {
                composeRule.onAllNodesWithTag("likesCount")
                    .onFirst()
                    .fetchSemanticsNode()
                    .config[SemanticsProperties.Text]
                    .firstOrNull()?.text?.toIntOrNull() ?: 0
            }.getOrDefault(0)
            actual > likesAntes
        }

        val likesDespues = runCatching {
            composeRule.onAllNodesWithTag("likesCount")
                .onFirst()
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        assert(likesDespues == likesAntes + 1) {
            "Likes esperados: ${likesAntes + 1}, obtenidos: $likesDespues"
        }

        // ── Paso 6: volver → reentrar → quitar like → verificar disminución ───
        composeRule.onNodeWithTag("btnBack").performClick() // salir de reviews

        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("albumDetalleScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("btnBack").performClick() // salir del detalle

        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("homeScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Volver a entrar al mismo álbum
        composeRule.onAllNodesWithTag("albumItem")
            .onFirst()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 12000) {
            composeRule.onAllNodesWithTag("albumDetalleScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Ir a reviews nuevamente
        composeRule.onNodeWithTag("btnVerResenas").performClick()

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag("resenaCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Leer likes antes de quitar el like
        val likesAntesUnlike = runCatching {
            composeRule.onAllNodesWithTag("likesCount")
                .onFirst()
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        // Quitar like (toggle)
        composeRule.onAllNodesWithTag("btnLike")
            .onFirst()
            .performClick()

        // Esperar a que el contador disminuya
        composeRule.waitUntil(timeoutMillis = 8000) {
            val actual = runCatching {
                composeRule.onAllNodesWithTag("likesCount")
                    .onFirst()
                    .fetchSemanticsNode()
                    .config[SemanticsProperties.Text]
                    .firstOrNull()?.text?.toIntOrNull() ?: 0
            }.getOrDefault(0)
            actual < likesAntesUnlike
        }

        val likesFinal = runCatching {
            composeRule.onAllNodesWithTag("likesCount")
                .onFirst()
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        assert(likesFinal == likesAntesUnlike - 1) {
            "Likes esperados tras unlike: ${likesAntesUnlike - 1}, obtenidos: $likesFinal"
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    @After
    fun cleanup() = runTest {
        try { FirebaseAuth.getInstance().currentUser?.delete()?.await() } catch (e: Exception) { }
        FirebaseAuth.getInstance().signOut()
    }
}
