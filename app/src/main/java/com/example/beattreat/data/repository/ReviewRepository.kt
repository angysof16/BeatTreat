package com.example.beattreat.data.repository

import com.example.beattreat.data.datasource.implementation.ReviewRetrofitDataSourceImplementation
import com.example.beattreat.ui.Resena.ResenaDetalladaUI
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val remoteDataSource: ReviewRetrofitDataSourceImplementation
) {
    suspend fun getReviewsByAlbum(albumId: Int): Result<List<ResenaDetalladaUI>> {
        return try {
            val dtos = remoteDataSource.getCommentsByAlbum(albumId)
            Result.success(dtos.map { it.toResenaDetalladaUI() })
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar las reseñas: ${e.message}"))
        }
    }
}