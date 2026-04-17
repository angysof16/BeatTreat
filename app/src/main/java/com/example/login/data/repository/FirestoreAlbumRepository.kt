// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/repository/FirestoreAlbumRepository.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.repository

import com.example.login.data.datasource.FirestoreAlbumRemoteDataSource
import com.example.login.ui.AlbumDetalle.AlbumDetalleUI
import com.example.login.ui.AlbumDetalle.CancionDetalleUI
import com.example.login.ui.Home.AlbumHomeUI
import com.example.login.ui.Home.ArtistaHomeUI
import javax.inject.Inject

class FirestoreAlbumRepository @Inject constructor(
    private val dataSource: FirestoreAlbumRemoteDataSource
) {

    suspend fun getAllAlbums(): Result<List<ArtistaHomeUI>> {
        return try {
            val albumsMap = dataSource.getAllAlbums()

            // Agrupamos por artista igual que el repositorio de Retrofit
            val agrupados = albumsMap.entries
                .groupBy { it.value.artist }
                .entries
                .mapIndexed { index, (artista, entries) ->
                    ArtistaHomeUI(
                        id      = index + 1,
                        nombre  = artista,
                        albumes = entries.map { (id, dto) ->
                            AlbumHomeUI(
                                id        = id.hashCode(),
                                nombre    = dto.title,
                                imagenUrl = dto.coverImage
                            )
                        }
                    )
                }

            Result.success(agrupados)
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar álbumes: ${e.message}"))
        }
    }

    suspend fun getAllAlbumsRaw(): Result<Map<String, com.example.login.data.dto.FirestoreAlbumDto>> {
        return try {
            Result.success(dataSource.getAllAlbums())
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar álbumes: ${e.message}"))
        }
    }

    suspend fun getAlbumById(albumId: String): Result<AlbumDetalleUI> {
        return try {
            val dto = dataSource.getAlbumById(albumId)
            val ui = AlbumDetalleUI(
                id                   = albumId.hashCode(),
                nombre               = dto.title,
                artista              = dto.artist,
                año                  = dto.releaseYear.toString(),
                genero               = dto.genre,
                descripcion          = dto.description,
                imagenUrl            = dto.coverImage,
                duracionTotal        = "—",
                calificacionPromedio = 0f,
                totalResenas         = 0,
                canciones            = emptyList()
            )
            Result.success(ui)
        } catch (e: Exception) {
            Result.failure(Exception("Álbum no encontrado: ${e.message}"))
        }
    }
}
