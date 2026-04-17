// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/repository/FirestoreUserRepository.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.repository

import com.example.login.data.datasource.AuthRemoteDataSource
import com.example.login.data.datasource.FirestoreUserRemoteDataSource
import com.example.login.data.dto.FirestoreUserDto
import com.example.login.data.dto.RegisterUserDto
import com.example.login.ui.Perfil.PerfilData
import com.example.login.ui.Perfil.PerfilUI
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirestoreUserRepository @Inject constructor(
    private val userDataSource: FirestoreUserRemoteDataSource,
    private val authDataSource: AuthRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) {

    // ── Registro ─────────────────────────────────────────────────────────────

    suspend fun registerUser(
        name: String,
        username: String,
        country: String?,
        bio: String?
    ): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: throw Exception("No se pudo obtener el usuario actual")

            val dto = RegisterUserDto(
                username = username,
                name     = name,
                country  = country,
                bio      = bio
            )
            userDataSource.registerUser(userId, dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Obtener mi perfil ─────────────────────────────────────────────────────

    suspend fun getMyProfile(): Result<PerfilUI> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: throw Exception("No hay sesión activa")

            val dto = userDataSource.getUserById(userId)
            val photoUrl = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""

            val perfil = PerfilUI(
                id            = 0,
                nombre        = dto.name.ifBlank { "Usuario" },
                usuario       = "@${dto.username}",
                fotoPerfilUrl = dto.profileImage ?: photoUrl,
                fotoBannerUrl = "",
                siguiendo     = 0,
                seguidores    = 0
            )
            // Sincroniza con PerfilData global para que el resto de la app lo use
            PerfilData.perfilActual = perfil
            Result.success(perfil)
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar perfil: ${e.message}"))
        }
    }

    // ── Obtener otro usuario ──────────────────────────────────────────────────

    suspend fun getUserById(userId: String): Result<FirestoreUserDto> {
        return try {
            val dto = userDataSource.getUserById(userId)
            Result.success(dto)
        } catch (e: Exception) {
            Result.failure(Exception("Usuario no encontrado: ${e.message}"))
        }
    }

    // ── Actualizar perfil ─────────────────────────────────────────────────────

    suspend fun updateProfile(
        name: String,
        username: String,
        bio: String?,
        profileImage: String?
    ): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: throw Exception("No hay sesión activa")

            val dto = FirestoreUserDto(
                username     = username,
                name         = name,
                bio          = bio,
                profileImage = profileImage
            )
            userDataSource.updateUser(userId, dto)

            // Actualiza también PerfilData global
            PerfilData.perfilActual = PerfilData.perfilActual.copy(
                nombre  = name,
                usuario = "@$username"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar perfil: ${e.message}"))
        }
    }

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
}
