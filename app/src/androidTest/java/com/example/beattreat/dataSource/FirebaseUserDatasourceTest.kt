package com.example.beattreat.dataSource

import com.example.beattreat.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
import com.example.beattreat.data.dto.RegisterUserDto
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Sprint 12 — Prueba de integración del UserFirestoreDataSource.
 * Se conecta al emulador de Firestore (10.0.2.2:8080).
 *
 * Estructura igual a la del profesor:
 *  @Before → conectar emulador, limpiar colección, cargar datos falsos
 *  @Test   → pruebas reales contra el emulador
 */
class FirebaseUserDatasourceTest {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var dataSource: UserFirestoreDataSourceImpl

    /** Genera un usuario falso con índice para hacer los datos únicos */
    private fun generateFakeUser(i: Int): Map<String, Any?> = mapOf(
        "username"       to "user_beat_$i",
        "name"           to "Beat User $i",
        "country"        to "Colombia",
        "bio"            to "Fan de la música número $i",
        "profileImage"   to null,
        "followersCount" to i * 10,
        "followingCount" to i * 5
    )

    @Before
    fun setup() = runTest {
        // Conectar al emulador (igual que el profe)
        try {
            db.useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) {
            // Ya configurado en tests anteriores — no pasa nada
        }

        dataSource = UserFirestoreDataSourceImpl(db)

        // Limpiar colección antes de cada test (igual que el profe)
        val users = db.collection("users").get().await()
        for (userDoc in users) {
            val followers = userDoc.reference.collection("followers").get().await()
            for (f in followers) f.reference.delete().await()

            val following = userDoc.reference.collection("following").get().await()
            for (f in following) f.reference.delete().await()

            userDoc.reference.delete().await()
        }

        // Cargar 10 usuarios de prueba (faker manual igual que el profe)
        val batch = db.batch()
        repeat(10) { i ->
            val userRef = db.collection("users").document("beatuser_$i")
            batch.set(userRef, generateFakeUser(i))
        }
        batch.commit().await()
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun getUserById_idValido_retornaUsuarioCorrecto() = runTest {
        val result = dataSource.getUserById("beatuser_5")

        assertThat(result).isNotNull()
        assertThat(result.name).isEqualTo("Beat User 5")
        assertThat(result.username).isEqualTo("user_beat_5")
    }

    @Test
    fun getUserById_idInvalido_lanzaExcepcion() = runTest {
        var thrown = false
        try {
            dataSource.getUserById("id_que_no_existe_xyz")
        } catch (e: Exception) {
            thrown = true
        }
        assertThat(thrown).isTrue()
    }

    @Test
    fun registerUser_insertaDocumento_documentoExiste() = runTest {
        val userId = "nuevo_beatuser_test"
        val dto = RegisterUserDto(
            username = "newbeatfan",
            name     = "Nuevo Fan BeatTreat",
            country  = "México",
            bio      = "Recién llegado a BeatTreat"
        )

        dataSource.registerUser(userId, dto)

        // Verificar que el documento existe
        val userResult = dataSource.getUserById(userId)

        assertThat(userResult).isNotNull()
        assertThat(userResult.name).isEqualTo("Nuevo Fan BeatTreat")
        assertThat(userResult.username).isEqualTo("newbeatfan")
        assertThat(userResult.profileImage).isNull()
    }

    @Test
    fun updateUser_actualizaNombreYBio_cambiosPersistidos() = runTest {
        val userId = "beatuser_3"

        // Obtener datos actuales
        val before = dataSource.getUserById(userId)
        assertThat(before.name).isEqualTo("Beat User 3")

        // Actualizar
        val updatedDto = com.example.beattreat.data.dto.FirestoreUserDto(
            username     = before.username,
            name         = "Nombre Actualizado Sprint12",
            bio          = "Bio actualizada",
            profileImage = null,
            country      = "Argentina"
        )
        dataSource.updateUser(userId, updatedDto)

        // Verificar cambios
        val after = dataSource.getUserById(userId)
        assertThat(after.name).isEqualTo("Nombre Actualizado Sprint12")
        assertThat(after.bio).isEqualTo("Bio actualizada")
        assertThat(after.country).isEqualTo("Argentina")
    }

    @Test
    fun registerUser_conBioNula_seGuardaSinBio() = runTest {
        val userId = "user_sin_bio_test"
        val dto = RegisterUserDto(
            username = "sinbio",
            name     = "Usuario Sin Bio",
            country  = "Perú",
            bio      = null
        )

        dataSource.registerUser(userId, dto)

        val result = dataSource.getUserById(userId)
        assertThat(result).isNotNull()
        assertThat(result.bio).isNull()
        assertThat(result.name).isEqualTo("Usuario Sin Bio")
    }

    @Test
    fun getUserById_despuesDeActualizarFoto_retornaUrlNueva() = runTest {
        val userId      = "beatuser_7"
        val nuevaFotoUrl = "https://storage.firebase.com/fotos/beatuser7.jpg"

        val dtoActual = dataSource.getUserById(userId)
        val updatedDto = com.example.beattreat.data.dto.FirestoreUserDto(
            username     = dtoActual.username,
            name         = dtoActual.name,
            bio          = dtoActual.bio,
            profileImage = nuevaFotoUrl,
            country      = dtoActual.country
        )
        dataSource.updateUser(userId, updatedDto)

        val result = dataSource.getUserById(userId)
        assertThat(result.profileImage).isEqualTo(nuevaFotoUrl)
    }

    @Test
    fun registerUser_multipleUsuarios_todosExisten() = runTest {
        val ids = listOf("bulk_user_a", "bulk_user_b", "bulk_user_c")
        ids.forEachIndexed { index, id ->
            dataSource.registerUser(
                id,
                RegisterUserDto(
                    username = "bulk_$index",
                    name     = "Bulk User $index",
                    country  = "Chile",
                    bio      = "Bio bulk $index"
                )
            )
        }

        ids.forEach { id ->
            val user = dataSource.getUserById(id)
            assertThat(user).isNotNull()
        }
    }

    @Test
    fun updateUser_soloActualizaUsername_restoSinCambios() = runTest {
        val userId = "beatuser_1"
        val before = dataSource.getUserById(userId)

        val updatedDto = com.example.beattreat.data.dto.FirestoreUserDto(
            username     = "nuevo_username_beat1",
            name         = before.name,      // mismo nombre
            bio          = before.bio,
            profileImage = before.profileImage,
            country      = before.country
        )
        dataSource.updateUser(userId, updatedDto)

        val after = dataSource.getUserById(userId)
        assertThat(after.username).isEqualTo("nuevo_username_beat1")
        assertThat(after.name).isEqualTo(before.name) // el nombre no cambió
    }

    @Test
    fun getUserById_primeraInserccion_nombreEsCorrecto() = runTest {
        val result = dataSource.getUserById("beatuser_0")

        assertThat(result.name).isEqualTo("Beat User 0")
        assertThat(result.username).isEqualTo("user_beat_0")
    }
}
