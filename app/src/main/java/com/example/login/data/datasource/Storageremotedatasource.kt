package com.example.login.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Datasource: Firebase Storage
class StorageRemoteDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {
    // Sube una imagen a Firebase Storage y devuelve la URL de descarga
    // path: ruta dentro del bucket, ej. "profileImages/userId.jpg"
    // uri:  URI local de la imagen elegida por el usuario
    suspend fun uploadImage(path: String, uri: Uri): String {
        val imageRef = storage.reference.child(path)
        imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }
}