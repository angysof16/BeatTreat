package com.example.beattreat.data.repository

import com.example.beattreat.data.datasource.implementation.UserRetrofitDataSourceImplementation
import com.example.beattreat.ui.PerfilOtroUsuario.OtroUsuarioUI
import com.example.beattreat.ui.PerfilOtroUsuario.ReviewOtroUsuarioUI
import javax.inject.Inject

/**
 * Repositorio de usuarios.
 *
 * Responsabilidades:
 *  - Recibir DTOs del data source.
 *  - Mapear a objetos de la capa visual (UI).
 *  - Envolver el resultado en Result<T>.
 *  - Capturar excepciones y transformarlas en mensajes amigables.
 */
class UserRepository @Inject constructor(
    private val remoteDataSource: UserRetrofitDataSourceImplementation
) {

    /**
     * Obtiene el perfil de un usuario por su ID.
     * Llama a GET /users/:id y mapea el DTO a [OtroUsuarioUI].
     */
    suspend fun getUserById(userId: Int): Result<OtroUsuarioUI> {
        return try {
            val dto = remoteDataSource.getUserById(userId)
            val ui = OtroUsuarioUI(
                id            = dto.id,
                nombre        = dto.name,
                username      = "@${dto.username}",
                bio           = dto.bio ?: "",
                fotoPerfilUrl = dto.profileImage ?: ""
            )
            Result.success(ui)

        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404)
                Result.failure(Exception("Usuario no encontrado"))
            else
                Result.failure(Exception("Error del servidor (${e.code()})"))

        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))

        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))

        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar el perfil: ${e.message}"))
        }
    }

    /**
     * Obtiene todos los reviews que ha escrito un usuario.
     * Llama a GET /users/:userId/reviews y mapea la lista de DTOs.
     */
    suspend fun getReviewsByUser(userId: Int): Result<List<ReviewOtroUsuarioUI>> {
        return try {
            val dtos = remoteDataSource.getReviewsByUser(userId)
            val reviews = dtos.map { dto ->
                ReviewOtroUsuarioUI(
                    id           = dto.id,
                    albumNombre  = dto.album?.title  ?: "Álbum desconocido",
                    albumArtista = dto.album?.artist ?: "",
                    rating       = dto.rating,
                    contenido    = dto.content,
                    // El backend devuelve ISO 8601; lo mostramos tal cual por ahora
                    fecha        = dto.createdAt?.take(10) ?: ""
                )
            }
            Result.success(reviews)

        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Error del servidor (${e.code()})"))

        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))

        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))

        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar los reviews: ${e.message}"))
        }
    }
}
