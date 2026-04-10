package com.example.login.data.repository

import com.example.login.data.datasource.implementation.AlbumRetrofitDataSourceImplementation
import com.example.login.data.dto.AlbumDto
import com.example.login.ui.AlbumDetalle.AlbumDetalleUI
import com.example.login.ui.Home.ArtistaHomeUI
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val remoteDataSource: AlbumRetrofitDataSourceImplementation
) {

    /**
     * Devuelve los DTOs crudos del backend. Útil para obtener IDs reales
     * cuando se va a enviar datos al servidor (ej: crear reseña).
     */
    suspend fun getAllAlbumsDto(): Result<List<AlbumDto>> {
        return try {
            Result.success(remoteDataSource.getAllAlbums())
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar los álbumes: ${e.message}"))
        }
    }

    // Trae todos los álbumes y los agrupa por artista para HomeScreen
    suspend fun getAllAlbums(): Result<List<ArtistaHomeUI>> {
        return try {
            val dtos = remoteDataSource.getAllAlbums()

            val agrupados = dtos
                .groupBy { it.artist }
                .entries
                .mapIndexed { index, (artista, albums) ->
                    ArtistaHomeUI(
                        id      = index + 1,
                        nombre  = artista,
                        albumes = albums.map { it.toAlbumHomeUI() }
                    )
                }

            Result.success(agrupados)

        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar los álbumes: ${e.message}"))
        }
    }

    // Trae el detalle de un álbum por id para AlbumDetalleScreen
    suspend fun getAlbumById(id: Int): Result<AlbumDetalleUI> {
        return try {
            val dto = remoteDataSource.getAlbumById(id)
            Result.success(dto.toAlbumDetalleUI())

        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) Result.failure(Exception("Álbum no encontrado"))
            else Result.failure(Exception("Error del servidor (${e.code()})"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar el álbum: ${e.message}"))
        }
    }
}