// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/datasource/implementation/firestore/UserFirestoreDataSourceImpl.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.datasource.implementation.firestore

import com.example.login.data.datasource.FirestoreUserRemoteDataSource
import com.example.login.data.dto.FirestoreUserDto
import com.example.login.data.dto.RegisterUserDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirestoreUserRemoteDataSource {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    override suspend fun registerUser(userId: String, dto: RegisterUserDto) {
        db.collection(USERS_COLLECTION)
            .document(userId)
            .set(dto)
            .await()
    }

    override suspend fun getUserById(userId: String): FirestoreUserDto {
        val snapshot = db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
        return snapshot.toObject(FirestoreUserDto::class.java)
            ?: throw Exception("No se pudo obtener los datos del usuario")
    }

    override suspend fun updateUser(userId: String, dto: FirestoreUserDto) {
        db.collection(USERS_COLLECTION)
            .document(userId)
            .set(dto)
            .await()
    }
}
