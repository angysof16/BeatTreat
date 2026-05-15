package com.example.beattreat.e2e

import androidx.compose.ui.semantics.SemanticsProperties
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
 * Sprint 13 — E2E Caso 2 (completo):
 *
 * Un usuario ya registrado realiza login. Va al perfil de otro usuario.
 * Verifica que la información del usuario sea correcta. Le da follow y
 * se verifica que aumenta la cantidad de seguidores. El usuario vuelve
 * al home y va a la sección de publicaciones de seguidos. Se verifica
 * que aparezca al menos una publicación del usuario que acaba de seguir.
 *
 * Supuesto: el usuario no sigue a nadie antes de iniciar la prueba.
 */
@HiltAndroidTest
class LoginFollowFeedE2E {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var authRepository: AuthRepository

    private val loginEmail    = "s13_login_e2e@beattreat.com"
    private val loginPassword = "123456"
    private val otroEmail     = "s13_otro_e2e@beattreat.com"
    private val otroPassword  = "123456"
    private var otroUserId    = ""

    @Before
    fun setup() {
        hiltRule.inject()

        try { FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099) } catch (e: Exception) { }
        try { FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080) } catch (e: Exception) { }

        try {
            androidx.test.platform.app.InstrumentationRegistry
                .getInstrumentation().uiAutomation
                .executeShellCommand(
                    "pm grant com.example.beattreat android.permission.POST_NOTIFICATIONS"
                )
        } catch (e: Exception) { }

        val auth      = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val authDS    = AuthRemoteDataSource(auth)
        val userDS    = UserFirestoreDataSourceImpl(firestore)
        val userRepo  = FirestoreUserRepository(userDS, auth)
        authRepository = AuthRepository(authDS)

        runBlocking {
            auth.signOut()

            // ── Crear / asegurar usuario principal ────────────────────────────
            val signUpMain = authRepository.signUp(loginEmail, loginPassword)
            if (signUpMain.isSuccess) {
                userRepo.registerUser(
                    name     = "Login E2E S13",
                    username = "logine2es13",
                    country  = "Colombia",
                    bio      = "Usuario principal Sprint 13"
                )
            }
            auth.signOut()

            // ── Crear / asegurar otro usuario con una review sembrada ──────────
            val signUpOtro = authRepository.signUp(otroEmail, otroPassword)
            if (signUpOtro.isSuccess) {
                otroUserId = auth.currentUser?.uid ?: ""
                userRepo.registerUser(
                    name     = "Otro E2E S13",
                    username = "otroe2es13",
                    country  = "Argentina",
                    bio      = "Fan de los álbumes raros"
                )
                // Sembrar una review para que aparezca en el feed
                firestore.collection("reviews").add(
                    mapOf(
                        "userId"     to otroUserId,
                        "albumId"    to "album_s13_seed",
                        "rating"     to 4.5f,
                        "content"    to "Review del Sprint 13 para el feed",
                        "createdAt"  to System.currentTimeMillis(),
                        "likesCount" to 0,
                        "user"       to mapOf(
                            "name"         to "Otro E2E S13",
                            "username"     to "otroe2es13",
                            "profileImage" to null
                        )
                    )
                ).await()
            } else {
                authRepository.signIn(otroEmail, otroPassword)
                otroUserId = auth.currentUser?.uid ?: ""
            }
            auth.signOut()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Smoke test: el login exitoso muestra la pantalla de perfil.
     */
    @Test
    fun login_exitoso_navegaAPerfilScreen() {
        composeRule.onNodeWithTag("loginScreen").assertIsDisplayed()

        composeRule.onNodeWithTag("emailField").performTextInput(loginEmail)
        composeRule.onNodeWithTag("passwordField").performTextInput(loginPassword)
        composeRule.onNodeWithTag("btnLogin").performClick()

        composeRule.waitUntil(timeoutMillis = 15000) {
            composeRule.onAllNodesWithTag("perfilScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("perfilScreen").assertIsDisplayed()
    }

    /**
     * Flujo completo E2E:
     *  1. Login del usuario principal.
     *  2. Buscar al otro usuario.
     *  3. Ir al perfil del otro usuario → verificar nombre y username.
     *  4. Dar follow → verificar que el contador de seguidores aumentó.
     *  5. Volver al home.
     *  6. Ir al feed de seguidos → verificar que aparece al menos una publicación.
     */
    @Test
    fun flujoCompleto_login_follow_verificarFeed() {

        // ── 1. Login ──────────────────────────────────────────────────────────
        composeRule.onNodeWithTag("loginScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("emailField").performTextInput(loginEmail)
        composeRule.onNodeWithTag("passwordField").performTextInput(loginPassword)
        composeRule.onNodeWithTag("btnLogin").performClick()

        composeRule.waitUntil(timeoutMillis = 15000) {
            composeRule.onAllNodesWithTag("perfilScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("perfilScreen").assertIsDisplayed()

        // ── 2. Ir al home ─────────────────────────────────────────────────────
        composeRule.onNodeWithTag("navHome").performClick()

        composeRule.waitUntil(timeoutMillis = 12000) {
            composeRule.onAllNodesWithTag("homeScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("homeScreen").assertIsDisplayed()

        // ── 3. Abrir el buscador ──────────────────────────────────────────────
        composeRule.onNodeWithTag("btnBuscar").performClick()

        composeRule.waitUntil(timeoutMillis = 12000) {
            composeRule.onAllNodesWithTag("buscarScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // ── 4. Buscar al otro usuario ─────────────────────────────────────────
        composeRule.onNodeWithTag("searchField").performTextInput("otroe2es13")

        composeRule.waitUntil(timeoutMillis = 15000) {
            composeRule.onAllNodesWithTag("usuarioResultado")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // ── 5. Ir al perfil del otro usuario ──────────────────────────────────
        composeRule.onAllNodesWithTag("usuarioResultado")
            .onFirst()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 12000) {
            composeRule.onAllNodesWithTag("perfilOtroUsuarioScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // ── 6. Verificar información del usuario ──────────────────────────────
        composeRule.onNodeWithTag("nombreOtroUsuario")
            .assertIsDisplayed()
            .assertTextContains("Otro E2E S13")

        composeRule.onNodeWithTag("usernameOtroUsuario")
            .assertTextContains("otroe2es13")

        // ── 7. Leer seguidores antes del follow ───────────────────────────────
        val seguidoresAntes = runCatching {
            composeRule.onNodeWithTag("seguidoresCount")
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        // ── 8. Dar follow ─────────────────────────────────────────────────────
        composeRule.onNodeWithTag("btnFollow").performClick()

        // Esperar a que el contador aumente
        composeRule.waitUntil(timeoutMillis = 15000) {
            val actual = runCatching {
                composeRule.onNodeWithTag("seguidoresCount")
                    .fetchSemanticsNode()
                    .config[SemanticsProperties.Text]
                    .firstOrNull()?.text?.toIntOrNull() ?: 0
            }.getOrDefault(0)
            actual > seguidoresAntes
        }

        val seguidoresDespues = runCatching {
            composeRule.onNodeWithTag("seguidoresCount")
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        assert(seguidoresDespues == seguidoresAntes + 1) {
            "Seguidores esperados: ${seguidoresAntes + 1}, obtenidos: $seguidoresDespues"
        }

        // Verificar que el botón cambió a "Siguiendo"
        composeRule.onNodeWithTag("btnFollow").assertTextContains("Siguiendo")

        // ── 9. Volver al home ─────────────────────────────────────────────────
        composeRule.onNodeWithTag("btnBack").performClick()

        // Puede volver a buscarScreen primero
        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("buscarScreen").fetchSemanticsNodes().isNotEmpty() ||
                    composeRule.onAllNodesWithTag("homeScreen").fetchSemanticsNodes().isNotEmpty()
        }

        if (composeRule.onAllNodesWithTag("buscarScreen").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("btnBack").performClick()
            composeRule.waitUntil(timeoutMillis = 8000) {
                composeRule.onAllNodesWithTag("homeScreen").fetchSemanticsNodes().isNotEmpty()
            }
        }

        composeRule.onNodeWithTag("homeScreen").assertIsDisplayed()

        // ── 10. Ir al feed de seguidos ────────────────────────────────────────
        composeRule.onNodeWithTag("navFeedSiguiendo").performClick()

        composeRule.waitUntil(timeoutMillis = 12000) {
            composeRule.onAllNodesWithTag("feedSiguiendoScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("feedSiguiendoScreen").assertIsDisplayed()

        // ── 11. Verificar que aparece al menos una publicación del seguido ─────
        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithTag("feedReviewCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithTag("feedReviewCard")
            .onFirst()
            .assertIsDisplayed()

        // Verificar que la publicación pertenece al usuario que seguimos
        composeRule.onAllNodesWithTag("feedReviewCard")
            .onFirst()
            .assertTextContains("otroe2es13")
    }

    // ─────────────────────────────────────────────────────────────────────────

    @After
    fun cleanup() = runTest {
        val auth      = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        try {
            val docs = firestore.collection("reviews")
                .whereEqualTo("albumId", "album_s13_seed")
                .get().await()
            docs.forEach { it.reference.delete().await() }
        } catch (e: Exception) { }

        try { auth.currentUser?.delete()?.await() } catch (e: Exception) { }
        auth.signOut()
    }
}
