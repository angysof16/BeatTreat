package com.example.beattreat.repository

import com.example.beattreat.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
import com.example.beattreat.data.dto.RegisterUserDto
import com.example.beattreat.data.repository.FirestoreUserRepository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Sprint 12 — Prueba de integración del FirestoreUserRepository.
 * Conecta al emulador de Firestore (10.0.2.2:8080).
 * Auth está mockeada (no necesitamos el emulador de Auth aquí).
 *
 * Estructura idéntica al profesor:
 *  @Before → emulador, limpiar, seed de datos
 *  @Test   → pruebas reales
 */
class UserRepositoryIntegrationTest {

    val db = FirebaseFirestore.getInstance()
    private lateinit var repository: FirestoreUserRepository

    private fun generateUser(i: Int): Map<String, Any?> = mapOf(
        "username"       to "beatuser_$i",
        "name"           to "Beat Name $i",
        "country"        to "Colombia",
        "bio"            to "Bio del usuario $i",
        "profileImage"   to null
    )

    @Before
    fun setup() = runTest {
        // Conectar al emulador
        try {
            db.useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) { /* ya configurado */ }

        // Mock de Auth
        val mockAuth = mockk<FirebaseAuth>()
        val mockUser = mockk<FirebaseUser>()
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "uid_integ_test"
        every { mockUser.photoUrl } returns null
        every { mockUser.displayName } returns "Integ Test User"

        val userDataSource = UserFirestoreDataSourceImpl(db)
        repository = FirestoreUserRepository(userDataSource, mockAuth)

        // Limpiar colección users antes de cada test (igual que el profe)
        val users = db.collection("users").get().await()
        for (userDoc in users) {
            val followers = userDoc.reference.collection("followers").get().await()
            for (f in followers) f.reference.delete().await()

            val following = userDoc.reference.collection("following").get().await()
            for (f in following) f.reference.delete().await()

            userDoc.reference.delete().await()
        }

        // Seed: cargar 10 usuarios de prueba
        val batch = db.batch()
        repeat(10) { i ->
            val userRef = db.collection("users").document("integ_user_$i")
            batch.set(userRef, generateUser(i))
        }
        batch.commit().await()
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun getUserById_idValido_retornaResultSuccess() = runTest {
        val result = repository.getUserById("integ_user_1")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.name).isEqualTo("Beat Name 1")
        assertThat(result.getOrNull()?.username).isEqualTo("beatuser_1")
    }

    @Test
    fun getUserById_idInvalido_retornaResultFailure() = runTest {
        val result = repository.getUserById("integ_user_99")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isNotNull()
    }

    @Test
    fun registerUser_creaDocumentoEnFirestore() = runTest {
        val result = repository.registerUser(
            name     = "Nuevo Registrado",
            username = "newreg_beat",
            country  = "Ecuador",
            bio      = "Me gustan los álbumes de rock"
        )

        assertThat(result.isSuccess).isTrue()

        // Verificar que el doc existe en Firestore con el uid del mock
        val doc = db.collection("users").document("uid_integ_test").get().await()
        assertThat(doc.exists()).isTrue()
        assertThat(doc.getString("name")).isEqualTo("Nuevo Registrado")
        assertThat(doc.getString("username")).isEqualTo("newreg_beat")
    }

    @Test
    fun getUserById_despuesDeRegistrar_retornaDatosCorrectos() = runTest {
        // Registrar directamente en Firestore
        db.collection("users").document("integ_user_check").set(
            mapOf(
                "username" to "checkuser",
                "name"     to "Check User",
                "country"  to "Venezuela",
                "bio"      to "Usuario para verificar",
                "profileImage" to null
            )
        ).await()

        val result = repository.getUserById("integ_user_check")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.name).isEqualTo("Check User")
        assertThat(result.getOrNull()?.country).isEqualTo("Venezuela")
    }

    @Test
    fun getUserById_primeraYUltimaInsercion_ambasExisten() = runTest {
        val first  = repository.getUserById("integ_user_0")
        val last   = repository.getUserById("integ_user_9")

        assertThat(first.isSuccess).isTrue()
        assertThat(last.isSuccess).isTrue()
        assertThat(first.getOrNull()?.name).isEqualTo("Beat Name 0")
        assertThat(last.getOrNull()?.name).isEqualTo("Beat Name 9")
    }

    @Test
    fun updateProfile_actualizaNombreEnFirestore() = runTest {
        // Primero insertar el usuario del mock
        db.collection("users").document("uid_integ_test").set(
            mapOf(
                "username"     to "olduser",
                "name"         to "Old Name",
                "country"      to "Perú",
                "bio"          to "Bio vieja",
                "profileImage" to null
            )
        ).await()

        val result = repository.updateProfile(
            name         = "Nombre Nuevo Sprint12",
            username     = "nuevousername",
            bio          = "Bio actualizada en test",
            profileImage = null
        )

        assertThat(result.isSuccess).isTrue()

        val doc = db.collection("users").document("uid_integ_test").get().await()
        assertThat(doc.getString("name")).isEqualTo("Nombre Nuevo Sprint12")
        assertThat(doc.getString("username")).isEqualTo("nuevousername")
    }

    @Test
    fun getUserById_multibusquedas_todasExitosas() = runTest {
        val ids = listOf("integ_user_2", "integ_user_5", "integ_user_8")

        ids.forEach { id ->
            val result = repository.getUserById(id)
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isNotNull()
        }
    }

    @Test
    fun getUserById_namePorIndice_correcto() = runTest {
        for (i in listOf(0, 3, 7)) {
            val result = repository.getUserById("integ_user_$i")
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.name).isEqualTo("Beat Name $i")
        }
    }
}
