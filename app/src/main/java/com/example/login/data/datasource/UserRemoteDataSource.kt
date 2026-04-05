package com.example.login.data.datasource

import com.example.login.data.dto.UserDto
import com.example.login.data.dto.UserReviewDto

/**
 * Contrato del data source de usuarios.
 * Cualquier implementación (Retrofit, Firestore, mock) debe cumplir este contrato.
 */
interface UserRemoteDataSource {
    suspend fun getUserById(id: Int): UserDto
    suspend fun getReviewsByUser(userId: Int): List<UserReviewDto>
}
