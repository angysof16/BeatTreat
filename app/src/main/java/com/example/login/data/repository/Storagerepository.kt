package com.example.login.data.repository

import android.net.Uri
import com.example.login.data.datasource.AuthRemoteDataSource
import com.example.login.data.datasource.StorageRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: StorageRemoteDataSource,
    private val auth: FirebaseAuth,
    private val authDataSource: AuthRemoteDataSource
) {
    // Sube la foto a Firebase Storage y además la guarda en el perfil de FirebaseAuth
    suspend fun uploadProfileImage(uri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))
            val path = "profileImages/$userId.jpg"
            val url  = storage.uploadImage(path, uri)

            // Guarda la URL también en FirebaseAuth (auth.currentUser.photoUrl)
            authDataSource.updateProfileImage(url)

            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadBannerImage(uri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))
            val path = "bannerImages/$userId.jpg"
            val url  = storage.uploadImage(path, uri)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}