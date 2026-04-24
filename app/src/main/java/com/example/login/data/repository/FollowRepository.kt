// FollowRepository.kt
package com.example.login.data.repository

import com.example.login.data.datasource.FollowRemoteDataSource
import com.example.login.ui.Seguidores.UsuarioUI
import javax.inject.Inject

/**
 * Repositorio del sistema de seguidores.
 *
 * Mapea DTOs de Firestore a entidades de UI (UsuarioUI)
 * y envuelve los resultados en Result<T>.
 */
class FollowRepository @Inject constructor(
    private val followDataSource: FollowRemoteDataSource
) {

    /**
     * Alterna el estado de seguir/dejar de seguir.
     * Retorna true si el usuario ahora sigue, false si dejó de seguir.
     */
    suspend fun followOrUnfollow(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            val isFollowing = followDataSource.followOrUnfollow(currentUserId, targetUserId)
            Result.success(isFollowing)
        } catch (e: Exception) {
            Result.failure(Exception("Error al procesar el follow: ${e.message}"))
        }
    }

    suspend fun isFollowing(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            Result.success(followDataSource.isFollowing(currentUserId, targetUserId))
        } catch (e: Exception) {
            Result.failure(Exception("Error al verificar seguimiento: ${e.message}"))
        }
    }

    suspend fun getFollowersCount(userId: String): Result<Int> {
        return try {
            Result.success(followDataSource.getFollowersCount(userId))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener seguidores: ${e.message}"))
        }
    }

    suspend fun getFollowingCount(userId: String): Result<Int> {
        return try {
            Result.success(followDataSource.getFollowingCount(userId))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener siguiendo: ${e.message}"))
        }
    }

    suspend fun getFollowerIds(userId: String): Result<List<String>> {
        return try {
            Result.success(followDataSource.getFollowerIds(userId))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener IDs de seguidores: ${e.message}"))
        }
    }

    suspend fun getFollowingIds(userId: String): Result<List<String>> {
        return try {
            Result.success(followDataSource.getFollowingIds(userId))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener IDs de seguidos: ${e.message}"))
        }
    }

    /**
     * Obtiene la lista de seguidores como UsuarioUI para mostrar en pantalla.
     * FIX: ahora incluye el firestoreId real
     */
    suspend fun getFollowersAsUI(userId: String): Result<List<UsuarioUI>> {
        return try {
            val ids   = followDataSource.getFollowerIds(userId)
            val dtos  = followDataSource.getFollowersUsers(userId)
            val users = ids.zip(dtos).mapIndexed { idx, (id, dto) ->
                UsuarioUI(
                    id = id.hashCode(),
                    firestoreId = id,  // ← CLAVE: guardar el UID real de Firebase
                    nombre = dto.name.ifBlank { "Usuario" },
                    usuario = "@${dto.username}"
                )
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener seguidores: ${e.message}"))
        }
    }

    /**
     * Obtiene la lista de seguidos como UsuarioUI para mostrar en pantalla.
     * FIX: ahora incluye el firestoreId real
     */
    suspend fun getFollowingAsUI(userId: String): Result<List<UsuarioUI>> {
        return try {
            val ids   = followDataSource.getFollowingIds(userId)
            val dtos  = followDataSource.getFollowingUsers(userId)
            val users = ids.zip(dtos).mapIndexed { idx, (id, dto) ->
                UsuarioUI(
                    id = id.hashCode(),
                    firestoreId = id,  // ← CLAVE: guardar el UID real de Firebase
                    nombre = dto.name.ifBlank { "Usuario" },
                    usuario = "@${dto.username}"
                )
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener seguidos: ${e.message}"))
        }
    }
}