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
                    id                   = id.hashCode(),
                    albumId              = albumId.hashCode(),
                    autorNombre          = dto.user.name.ifBlank { "Usuario" },
                    autorUsuario         = "@${dto.user.username}",
                    autorFotoUrl         = dto.user.profileImage ?: "",
                    albumNombre          = "",
                    albumArtista         = "",
                    albumImagenUrl       = "",
                    calificacion         = dto.rating,
                    texto                = dto.content,
                    likes                = 0,
                    comentarios          = 0,
                    fecha                = formatTimestamp(dto.createdAt),
                    autorFirestoreUserId = dto.userId,
                    autorUserId          = if (dto.userId.isNotBlank()) 1 else 0
                )
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar reviews: ${e.message}"))
        }
    }

    // ← NUEVO: obtiene reviews de un usuario para mostrarse en PerfilOtroUsuario
    suspend fun getReviewsByUser(userId: String): Result<List<ResenaDetalladaUI>> {
        return try {
            val pairs = dataSource.getReviewsByUser(userId)
            val reviews = pairs.map { (id, dto) ->
                ResenaDetalladaUI(
                    id                   = id.hashCode(),
                    albumId              = dto.albumId.hashCode(),
                    autorNombre          = dto.user.name.ifBlank { "Usuario" },
                    autorUsuario         = "@${dto.user.username}",
                    autorFotoUrl         = dto.user.profileImage ?: "",
                    albumNombre          = "",
                    albumArtista         = "",
                    albumImagenUrl       = "",
                    calificacion         = dto.rating,
                    texto                = dto.content,
                    likes                = 0,
                    comentarios          = 0,
                    fecha                = formatTimestamp(dto.createdAt),
                    autorFirestoreUserId = dto.userId,
                    autorUserId          = if (dto.userId.isNotBlank()) 1 else 0
                )
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar reviews del usuario: ${e.message}"))
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

            // ← FIX: getOrNull() en lugar del cast roto con getOrElse
            val userDto = userRepository.getUserById(userId).getOrNull()

            val reviewDto = FirestoreReviewDto(
                userId    = userId,
                albumId   = albumId,
                rating    = rating,
                content   = content,
                createdAt = System.currentTimeMillis(),
                user      = FirestoreReviewUserDto(
                    // ← FIX: ahora userDto es FirestoreUserDto directamente, sin cast
                    name         = userDto?.name?.takeIf { it.isNotBlank() }
                        ?: currentUser.displayName
                        ?: "Usuario",
                    username     = userDto?.username?.takeIf { it.isNotBlank() } ?: "",
                    profileImage = userDto?.profileImage
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