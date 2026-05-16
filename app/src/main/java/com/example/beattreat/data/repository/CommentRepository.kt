package com.example.beattreat.data.repository

import com.example.beattreat.data.datasource.CommentFirestoreDataSource
import com.example.beattreat.data.dto.FirestoreCommentDto
import com.example.beattreat.data.dto.FirestoreReviewUserDto
import com.example.beattreat.ui.Resena.ComentarioUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val dataSource: CommentFirestoreDataSource,
    private val userRepository: FirestoreUserRepository,
    private val firebaseAuth: FirebaseAuth
) {

    fun listenComments(reviewId: String): Flow<List<ComentarioUI>> =
        dataSource.listenCommentsByReview(reviewId).map { pairs ->
            pairs.mapIndexed { index, (docId, dto) ->
                ComentarioUI(
                    id           = docId.hashCode(),
                    autorNombre  = dto.user.name.ifBlank { "Usuario" },
                    autorUsuario = "@${dto.user.username}",
                    autorFotoUrl = dto.user.profileImage ?: "",
                    texto        = dto.content,
                    likes        = 0,
                    fecha        = formatTimestamp(dto.createdAt)
                )
            }
        }

    suspend fun addComment(reviewId: String, content: String): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: throw Exception("Debes iniciar sesión para comentar")
            val userId  = currentUser.uid
            val userDto = userRepository.getUserById(userId).getOrNull()

            val dto = FirestoreCommentDto(
                reviewId  = reviewId,
                userId    = userId,
                content   = content,
                createdAt = System.currentTimeMillis(),
                user      = FirestoreReviewUserDto(
                    name         = userDto?.name?.takeIf { it.isNotBlank() } ?: currentUser.displayName ?: "Usuario",
                    username     = userDto?.username?.takeIf { it.isNotBlank() } ?: "",
                    profileImage = userDto?.profileImage ?: currentUser.photoUrl?.toString()
                )
            )
            dataSource.addComment(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al enviar comentario: ${e.message}"))
        }
    }

    private fun formatTimestamp(ts: Long): String {
        if (ts == 0L) return ""
        return try {
            val now  = System.currentTimeMillis()
            val diff = now - ts
            when {
                diff < 60_000L     -> "Hace ${diff / 1_000} segundos"
                diff < 3_600_000L  -> "Hace ${diff / 60_000} minutos"
                diff < 86_400_000L -> "Hace ${diff / 3_600_000} horas"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
            }
        } catch (e: Exception) { "" }
    }
}