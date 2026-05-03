package com.example.login.data.datasource

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class StorageRemoteDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {

    suspend fun uploadImage(path: String, uri: Uri): String {
        val imageRef = storage.reference.child(path)

        // Convertir URI a bytes comprimidos
        val imageBytes = uriToCompressedBytes(uri)

        // Subir los bytes directamente
        imageRef.putBytes(imageBytes).await()

        return imageRef.downloadUrl.await().toString()
    }

    private fun uriToCompressedBytes(uri: Uri): ByteArray {
        val inputStream: InputStream = when (uri.scheme) {
            "content" -> {
                val resolver = storage.app.applicationContext.contentResolver
                resolver.openInputStream(uri) ?: throw IllegalArgumentException("No se puede abrir el URI: $uri")
            }
            "file" -> {
                uri.path?.let { java.io.File(it).inputStream() }
                    ?: throw IllegalArgumentException("No se encuentra el archivo: $uri")
            }
            else -> throw IllegalArgumentException("URI no soportado: $uri")
        }

        inputStream.use { stream ->
            // Decodificar bitmap con opciones para reducir memoria
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(stream, null, options)

            // Calcular factor de muestreo para que la imagen no supere 1024x1024
            var sampleSize = 1
            val maxDimension = 1024
            while ((options.outWidth / sampleSize) > maxDimension ||
                (options.outHeight / sampleSize) > maxDimension) {
                sampleSize *= 2
            }

            // Decodificar con el factor de muestreo
            options.inJustDecodeBounds = false
            options.inSampleSize = sampleSize

            // Volver a abrir el stream (no se puede reusar)
            val newStream = when (uri.scheme) {
                "content" -> {
                    val resolver = storage.app.applicationContext.contentResolver
                    resolver.openInputStream(uri) ?: throw IllegalArgumentException("No se puede abrir el URI: $uri")
                }
                "file" -> {
                    uri.path?.let { java.io.File(it).inputStream() }
                        ?: throw IllegalArgumentException("No se encuentra el archivo: $uri")
                }
                else -> throw IllegalArgumentException("URI no soportado: $uri")
            }

            newStream.use { secondStream ->
                val bitmap = BitmapFactory.decodeStream(secondStream, null, options)

                // Comprimir a JPEG con calidad 80%
                val outputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

                return outputStream.toByteArray()
            }
        }
    }
}