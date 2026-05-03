package com.example.beattreat.data.repository

import android.net.Uri
import com.example.beattreat.data.datasource.AuthRemoteDataSource
import com.example.beattreat.data.datasource.StorageRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await  // ← AÑADIR ESTA IMPORTACIÓN
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: StorageRemoteDataSource,
    private val auth: FirebaseAuth,
    private val authDataSource: AuthRemoteDataSource
) {

    suspend fun uploadProfileImage(uri: Uri): Result<String> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("Debes iniciar sesión para cambiar tu foto"))
            }

            // Refrescar token antes de subir
            currentUser.getIdToken(true).await()  // ← AHORA SÍ FUNCIONA

            val userId = currentUser.uid
            val path = "profileImages/$userId"

            val url = storage.uploadImage(path, uri)

            // Actualizar URL en FirebaseAuth y Firestore
            authDataSource.updateProfileImage(url)

            Result.success(url)

        } catch (e: StorageException) {
            val msg = when (e.errorCode) {
                StorageException.ERROR_NOT_AUTHORIZED ->
                    "Sin permiso para subir imágenes. Token expirado, cierra sesión y vuelve a iniciar."
                StorageException.ERROR_QUOTA_EXCEEDED ->
                    "Almacenamiento lleno. Intenta más tarde."
                StorageException.ERROR_CANCELED ->
                    "Subida cancelada."
                else ->
                    "Error al subir la imagen: ${e.message}"
            }
            Result.failure(Exception(msg))

        } catch (e: Exception) {
            Result.failure(Exception("Error al subir foto: ${e.message}"))
        }
    }
}