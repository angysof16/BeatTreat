// data/datasource/FirestoreUserRemoteDataSource.kt
package com.example.login.data.datasource

import com.example.login.data.dto.FirestoreUserDto
import com.example.login.data.dto.RegisterUserDto

interface FirestoreUserRemoteDataSource {
    suspend fun registerUser(userId: String, dto: RegisterUserDto)
    suspend fun getUserById(userId: String): FirestoreUserDto
    suspend fun updateUser(userId: String, dto: FirestoreUserDto)
}