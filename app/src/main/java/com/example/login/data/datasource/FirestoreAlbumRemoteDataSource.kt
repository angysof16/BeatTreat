// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/datasource/FirestoreAlbumRemoteDataSource.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.datasource

import com.example.login.data.dto.FirestoreAlbumDto

interface FirestoreAlbumRemoteDataSource {
    suspend fun getAllAlbums(): Map<String, FirestoreAlbumDto>
    suspend fun getAlbumById(albumId: String): FirestoreAlbumDto
}
