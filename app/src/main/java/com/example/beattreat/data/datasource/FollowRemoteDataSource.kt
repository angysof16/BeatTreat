package com.example.beattreat.data.datasource

import com.example.beattreat.data.dto.FirestoreUserDto

/**
 * Contrato para operaciones de Seguir/Dejar de seguir.
 *
 * Subcolecciones usadas (patrón del profesor):
 *   users/{currentUserId}/following/{targetUserId}
 *   users/{targetUserId}/followers/{currentUserId}
 *
 * Cada operación de follow/unfollow es una TRANSACCIÓN de 4 pasos:
 *   1. Agrega/elimina doc en following del usuario actual
 *   2. Agrega/elimina doc en followers del usuario destino
 *   3. Incrementa/decrementa followingCount del usuario actual
 *   4. Incrementa/decrementa followersCount del usuario destino
 *
 * listenToFollowingReviews: Flow de datos en tiempo real de reviews
 *   de todos los usuarios que sigo (para el feed "siguiendo").
 */
interface FollowRemoteDataSource {
    suspend fun followOrUnfollow(currentUserId: String, targetUserId: String): Boolean // true = siguiendo
    suspend fun isFollowing(currentUserId: String, targetUserId: String): Boolean
    suspend fun getFollowersCount(userId: String): Int
    suspend fun getFollowingCount(userId: String): Int
    suspend fun getFollowerIds(userId: String): List<String>
    suspend fun getFollowingIds(userId: String): List<String>
    suspend fun getFollowersUsers(userId: String): List<FirestoreUserDto>
    suspend fun getFollowingUsers(userId: String): List<FirestoreUserDto>
}
