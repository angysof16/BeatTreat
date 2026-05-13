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
 * E2E Caso 2:
 *
 * Un usuario ya registrado realiza login. Busca a otro usuario.
 * Verifica que la información del usuario sea correcta. Le da follow
 * y se verifica que aumenta la cantidad de seguidores. Vuelve al home
 * y va a la sección de publicaciones de seguidos. Verifica que aparezca
 * al menos una publicación del usuario que siguió.
 */
@HiltAndroidTest
class LoginAndFollowE2E {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var authRepository: AuthRepository

    private val loginEmail    = "usuario_login_e2e@beattreat.com"
    private val loginPassword = "123456"
    private val otroEmail     = "otro_usuario_e2e@beattreat.com"
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
        val authDataSource = AuthRemoteDataSource(auth)
        val userDataSource = UserFirestoreDataSourceImpl(firestore)
        val userRepo       = FirestoreUserRepository(userDataSource, auth)
        authRepository     = AuthRepository(authDataSource)

        runBlocking {
            // Garantizar que no hay sesión activa al iniciar el test
            auth.signOut()

            // ── Usuario principal ─────────────────────────────────────────────
            val signUpPrincipal = authRepository.signUp(loginEmail, loginPassword)
            if (signUpPrincipal.isSuccess) {
                userRepo.registerUser(
                    name     = "Usuario Login E2E",
                    username = "usuariologine2e",
                    country  = "Colombia",
                    bio      = "Usuario de prueba"
                )
            }
            auth.signOut()

            // ── Otro usuario + review sembrada ────────────────────────────────
            val signUpOtro = authRepository.signUp(otroEmail, otroPassword)
            if (signUpOtro.isSuccess) {
                val uid = auth.currentUser?.uid ?: ""
                otroUserId = uid
                userRepo.registerUser(
                    name     = "Otro Usuario E2E",
                    username = "otrousuarioe2e",
                    country  = "Argentina",
                    bio      = "Me encanta la música"
                )
                firestore.collection("reviews").add(
                    mapOf(
                        "userId"     to uid,
                        "albumId"    to "album_e2e_seed",
                        "rating"     to 4.5f,
                        "content"    to "Reseña de prueba E2E para el feed de seguidos",
                        "createdAt"  to System.currentTimeMillis(),
                        "likesCount" to 0,
                        "user"       to mapOf(
                            "name"         to "Otro Usuario E2E",
                            "username"     to "otrousuarioe2e",
                            "profileImage" to null
                        )
                    )
                ).await()
            } else {
                // Ya existe — solo necesitamos su UID
                authRepository.signIn(otroEmail, otroPassword)
                otroUserId = auth.currentUser?.uid ?: ""
            }
            auth.signOut()
        }
    }

    // ── Test 1: smoke test de login ───────────────────────────────────────────

    @Test
    fun login_exitoso_muestraPantallaInicial() {
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

    // ── Test 2: flujo completo ────────────────────────────────────────────────

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

        // ── 2. Ir al home desde el perfil ─────────────────────────────────────
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
        composeRule.onNodeWithTag("buscarScreen").assertIsDisplayed()

        // ── 4. Buscar al otro usuario ─────────────────────────────────────────
        composeRule.onNodeWithTag("searchField").performTextInput("otrousuarioe2e")

        // La búsqueda puede tener debounce; esperar con tolerancia
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
            .assertTextContains("Otro Usuario E2E")

        composeRule.onNodeWithTag("usernameOtroUsuario")
            .assertTextContains("otrousuarioe2e")

        // ── 7. Leer seguidores antes del follow ───────────────────────────────
        val seguidoresAntes = runCatching {
            composeRule
                .onNodeWithTag("seguidoresCount")
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        // ── 8. Dar follow ─────────────────────────────────────────────────────
        composeRule.onNodeWithTag("btnFollow").performClick()

        // Esperar a que Firestore confirme y el ViewModel actualice el contador
        composeRule.waitUntil(timeoutMillis = 15000) {
            val actual = runCatching {
                composeRule
                    .onNodeWithTag("seguidoresCount")
                    .fetchSemanticsNode()
                    .config[SemanticsProperties.Text]
                    .firstOrNull()?.text?.toIntOrNull() ?: 0
            }.getOrDefault(0)
            actual > seguidoresAntes
        }

        val seguidoresDespues = runCatching {
            composeRule
                .onNodeWithTag("seguidoresCount")
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .firstOrNull()?.text?.toIntOrNull() ?: 0
        }.getOrDefault(0)

        assert(seguidoresDespues == seguidoresAntes + 1) {
            "Seguidores esperados: ${seguidoresAntes + 1}, actuales: $seguidoresDespues"
        }

        // Verificar que el botón cambió a "Siguiendo"
        composeRule.onNodeWithTag("btnFollow").assertTextContains("Siguiendo")

        // ── 9. Volver al home ─────────────────────────────────────────────────
        composeRule.onNodeWithTag("btnBack").performClick()

        // Puede volver a buscarScreen primero
        composeRule.waitUntil(timeoutMillis = 8000) {
            composeRule.onAllNodesWithTag("buscarScreen")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeRule.onAllNodesWithTag("homeScreen")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        // Si volvió a buscarScreen, salir también de ahí
        val enBuscar = composeRule.onAllNodesWithTag("buscarScreen")
            .fetchSemanticsNodes().isNotEmpty()
        if (enBuscar) {
            composeRule.onNodeWithTag("btnBack").performClick()
            composeRule.waitUntil(timeoutMillis = 8000) {
                composeRule.onAllNodesWithTag("homeScreen")
                    .fetchSemanticsNodes().isNotEmpty()
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

        // ── 11. Verificar que aparece la publicación del usuario seguido ───────
        // El listener en tiempo real puede tardar; se da más margen
        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithTag("feedReviewCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithTag("feedReviewCard")
            .onFirst()
            .assertIsDisplayed()

        composeRule.onAllNodesWithTag("feedReviewCard")
            .onFirst()
            .assertTextContains("otrousuarioe2e")
    }

    // ─────────────────────────────────────────────────────────────────────────

    @After
    fun cleanup() = runTest {
        val auth      = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        try {
            val docs = firestore.collection("reviews")
                .whereEqualTo("albumId", "album_e2e_seed")
                .get().await()
            docs.forEach { it.reference.delete().await() }
        } catch (e: Exception) { }

        try { auth.currentUser?.delete()?.await() } catch (e: Exception) { }
        auth.signOut()
    }
}