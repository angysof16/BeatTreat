// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/datasource/implementation/firestore/AlbumFirestoreDataSourceImpl.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.datasource.implementation.firestore

import com.example.login.data.datasource.FirestoreAlbumRemoteDataSource
import com.example.login.data.dto.FirestoreAlbumDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlbumFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirestoreAlbumRemoteDataSource {

    companion object {
        private const val ALBUMS_COLLECTION = "albums"
    }

    override suspend fun getAllAlbums(): Map<String, FirestoreAlbumDto> {
        val snapshot = db.collection(ALBUMS_COLLECTION)
            .get()
            .await()
        return snapshot.documents.associate { doc ->
            val album = doc.toObject(FirestoreAlbumDto::class.java)
                ?: throw Exception("Error al parsear álbum ${doc.id}")
            doc.id to album
        }
    }

    override suspend fun getAlbumById(albumId: String): FirestoreAlbumDto {
        val snapshot = db.collection(ALBUMS_COLLECTION)
            .document(albumId)
            .get()
            .await()
        return snapshot.toObject(FirestoreAlbumDto::class.java)
            ?: throw Exception("Álbum no encontrado")
    }
}
