// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/repository/FirestoreReviewRepository.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.repository

import com.example.login.data.datasource.FirestoreReviewRemoteDataSource
import com.example.login.data.dto.FirestoreReviewDto
import com.example.login.data.dto.FirestoreReviewUserDto
import com.example.login.ui.Resena.ResenaDetalladaUI
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FirestoreReviewRepository @Inject constructor(
    private val dataSource: FirestoreReviewRemoteDataSource,
    private val userRepository: FirestoreUserRepository,
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun getReviewsByAlbum(albumId: String): Result<List<ResenaDetalladaUI>> {
        return try {
            val pairs = dataSource.getReviewsByAlbum(albumId)

            val reviews = pairs.map { (id, dto) ->
                ResenaDetalladaUI(
                    id             = id.hashCode(),
                    albumId        = albumId.hashCode(),
                    autorNombre    = dto.user.name.ifBlank { "Usuario" },
                    autorUsuario   = "@${dto.user.username}",
                    autorFotoUrl   = dto.user.profileImage ?: "",
                    albumNombre    = "",
                    albumArtista   = "",
                    albumImagenUrl = "",
                    calificacion   = dto.rating,
                    texto          = dto.content,
                    likes          = 0,
                    comentarios    = 0,
                    fecha          = formatTimestamp(dto.createdAt),
                    autorUserId    = 0
                )
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar reviews: ${e.message}"))
        }
    }

    suspend fun createReview(
        albumId: String,
        rating: Float,
        content: String
    ): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: throw Exception("Debes iniciar sesión para escribir una reseña")

            val userId = currentUser.uid

            // Obtenemos datos del usuario para desnormalizar
            val userDto = userRepository.getUserById(userId)
                .getOrElse { FirestoreReviewUserDto().let { return@getOrElse null } }

            val reviewDto = FirestoreReviewDto(
                userId    = userId,
                albumId   = albumId,
                rating    = rating,
                content   = content,
                createdAt = System.currentTimeMillis(),
                user      = FirestoreReviewUserDto(
                    name         = (userDto as? com.example.login.data.dto.FirestoreUserDto)?.name
                        ?: currentUser.displayName ?: "Usuario",
                    username     = (userDto as? com.example.login.data.dto.FirestoreUserDto)?.username ?: "",
                    profileImage = (userDto as? com.example.login.data.dto.FirestoreUserDto)?.profileImage
                        ?: currentUser.photoUrl?.toString()
                )
            )

            dataSource.createReview(reviewDto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear review: ${e.message}"))
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        if (timestamp == 0L) return ""
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
}
