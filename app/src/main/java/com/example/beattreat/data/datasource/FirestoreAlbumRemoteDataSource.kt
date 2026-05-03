// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/datasource/FirestoreAlbumRemoteDataSource.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.beattreat.data.datasource

import com.example.beattreat.data.dto.FirestoreAlbumDto

interface FirestoreAlbumRemoteDataSource {
    suspend fun getAllAlbums(): Map<String, FirestoreAlbumDto>
    suspend fun getAlbumById(albumId: String): FirestoreAlbumDto
}
