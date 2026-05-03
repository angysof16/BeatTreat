// data/repository/FirestoreUserRepository.kt (CORREGIDO)
package com.example.beattreat.data.repository

import com.example.beattreat.data.datasource.FirestoreUserRemoteDataSource
import com.example.beattreat.data.dto.FirestoreUserDto
import com.example.beattreat.data.dto.RegisterUserDto
import com.example.beattreat.ui.Perfil.PerfilData
import com.example.beattreat.ui.Perfil.PerfilUI
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirestoreUserRepository @Inject constructor(
    private val userDataSource: FirestoreUserRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) {

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
                name = name,
                country = country,
                bio = bio
            )
            userDataSource.registerUser(userId, dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyProfile(): Result<PerfilUI> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: throw Exception("No hay sesión activa")

            val dto = userDataSource.getUserById(userId)
            val photoUrl = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""

            val perfil = PerfilUI(
                id = 0,
                nombre = dto.name.ifBlank { "Usuario" },
                usuario = "@${dto.username}",
                fotoPerfilUrl = dto.profileImage ?: photoUrl,
                fotoBannerUrl = "",
                siguiendo = 0,
                seguidores = 0,
                bio = dto.bio ?: ""  // ← AÑADIR BIO
            )
            // Sincroniza con PerfilData global
            PerfilData.perfilActual = perfil
            Result.success(perfil)
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar perfil: ${e.message}"))
        }
    }

    suspend fun getUserById(userId: String): Result<FirestoreUserDto> {
        return try {
            val dto = userDataSource.getUserById(userId)
            Result.success(dto)
        } catch (e: Exception) {
            Result.failure(Exception("Usuario no encontrado: ${e.message}"))
        }
    }

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
                username = username,
                name = name,
                bio = bio,  // ← AHORA GUARDA LA BIO
                profileImage = profileImage
            )
            userDataSource.updateUser(userId, dto)

            // Actualiza también PerfilData global con la bio
            PerfilData.perfilActual = PerfilData.perfilActual.copy(
                nombre = name,
                usuario = "@$username",
                bio = bio ?: ""  // ← ACTUALIZAR BIO
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar perfil: ${e.message}"))
        }
    }

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
}