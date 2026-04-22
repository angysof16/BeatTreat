package com.example.login.data.dto

/**
 * Documento guardado en subcolecciones de usuarios:
 *   users/{userId}/followers/{followerId}
 *   users/{userId}/following/{followingId}
 *
 * El ID del documento ES el userId del otro extremo de la relación.
 * Siguiendo el patrón del profesor para relaciones muchos-a-muchos.
 */
data class FollowDto(
    val timestamp: Long = System.currentTimeMillis()
)
