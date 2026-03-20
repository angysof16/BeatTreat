package com.example.login.data.repository

import android.net.Uri
import com.example.login.data.datasource.AuthRemoteDataSource
import com.example.login.data.datasource.StorageRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageException
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: StorageRemoteDataSource,
    private val auth: FirebaseAuth,
    private val authDataSource: AuthRemoteDataSource
) {
    // Sube la foto a Firebase Storage
    // guarda en el perfil de FirebaseAuth
    suspend fun uploadProfileImage(uri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Debes iniciar sesión para cambiar tu foto"))

            val path = "profileImages/$userId.jpg"
            val url  = storage.uploadImage(path, uri)

            // Guarda la URL también en FirebaseAuth
            // auth.currentUser.photoUrl
            authDataSource.updateProfileImage(url)

            Result.success(url)

        } catch (e: StorageException) {
            // ERRORES SDE FIREBASE STORAGE
            val msg = when (e.errorCode) {
                StorageException.ERROR_QUOTA_EXCEEDED ->
                    "Almacenamiento lleno. Intenta más tarde."
                StorageException.ERROR_NOT_AUTHORIZED ->
                    "Sin permiso para subir imágenes. Revisa tu sesión."
                StorageException.ERROR_CANCELED ->
                    "Subida cancelada."
                StorageException.ERROR_OBJECT_NOT_FOUND ->
                    "No se encontró el archivo. Intenta de nuevo."
                StorageException.ERROR_RETRY_LIMIT_EXCEEDED ->
                    "La subida tardó demasiado. Verifica tu conexión."
                else ->
                    "Error al subir la imagen (código ${e.httpResultCode})."
            }
            Result.failure(Exception(msg))

        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))

        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))

        } catch (e: Exception) {
            Result.failure(Exception("No se pudo subir la foto. Intenta de nuevo."))
        }
    }

    suspend fun uploadBannerImage(uri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Debes iniciar sesión para cambiar el banner"))

            val path = "bannerImages/$userId.jpg"
            val url  = storage.uploadImage(path, uri)
            Result.success(url)

        } catch (e: StorageException) {
            val msg = when (e.errorCode) {
                StorageException.ERROR_QUOTA_EXCEEDED ->
                    "Almacenamiento lleno. Intenta más tarde."
                StorageException.ERROR_NOT_AUTHORIZED ->
                    "Sin permiso para subir imágenes. Revisa tu sesión."
                StorageException.ERROR_CANCELED ->
                    "Subida cancelada."
                StorageException.ERROR_RETRY_LIMIT_EXCEEDED ->
                    "La subida tardó demasiado. Verifica tu conexión."
                else ->
                    "Error al subir el banner (código ${e.httpResultCode})."
            }
            Result.failure(Exception(msg))

        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu red."))

        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("La conexión tardó demasiado. Intenta de nuevo."))

        } catch (e: Exception) {
            Result.failure(Exception("No se pudo subir el banner. Intenta de nuevo."))
        }
    }
}