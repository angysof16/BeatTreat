package com.example.login.data.repository

import com.example.login.data.datasource.implementation.MiReviewRetrofitDataSource
import com.example.login.data.dto.CreateReviewDTO
import com.example.login.data.dto.UpdateReviewDTO
import com.example.login.ui.MiPerfil.MiResenaUI
import javax.inject.Inject

class MiPerfilRepository @Inject constructor(
    private val remoteDataSource: MiReviewRetrofitDataSource
) {

    companion object {
        /** Usuario hardcodeado = 1 según requerimiento Persona 3 */
        const val MI_USER_ID = 1
    }

    /** GET /users/1/reviews → lista de reseñas del usuario actual */
    suspend fun getMisResenas(): Result<List<MiResenaUI>> {
        return try {
            val dtos = remoteDataSource.getReviewsByUser(MI_USER_ID)
            val lista = dtos.map { dto ->
                MiResenaUI(
                    id          = dto.id,
                    albumId     = dto.albumId,
                    albumTitulo = dto.album?.title ?: "Álbum desconocido",
                    albumArtist = dto.album?.artist ?: "",
                    albumCover  = dto.album?.coverImage ?: "",
                    rating      = dto.rating,
                    content     = dto.content,
                    createdAt   = dto.createdAt ?: ""
                )
            }
            Result.success(lista)
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar tus reseñas: ${e.message}"))
        }
    }

    /** POST /reviews → crea una nueva reseña */
    suspend fun crearResena(albumId: Int, rating: Float, content: String): Result<Unit> {
        return try {
            val body = CreateReviewDTO(
                userId  = MI_USER_ID,
                albumId = albumId,
                rating  = rating,
                content = content
            )
            remoteDataSource.createReview(body)
            Result.success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear la reseña: ${e.message}"))
        }
    }

    /** PUT /reviews/:id → edita rating y content */
    suspend fun editarResena(reviewId: Int, rating: Float, content: String): Result<Unit> {
        return try {
            remoteDataSource.updateReview(reviewId, UpdateReviewDTO(rating, content))
            Result.success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al editar la reseña: ${e.message}"))
        }
    }

    /** DELETE /reviews/:id → elimina la reseña */
    suspend fun eliminarResena(reviewId: Int): Result<Unit> {
        return try {
            remoteDataSource.deleteReview(reviewId)
            Result.success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al eliminar la reseña: ${e.message}"))
        }
    }
}
