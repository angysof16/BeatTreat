package com.example.beattreat.data.datasource

import com.example.beattreat.data.dto.AlbumDto

// Contrato del data source de álbumes
interface AlbumRemoteDataSource {
    suspend fun getAllAlbums(): List<AlbumDto>
    suspend fun getAlbumById(id: Int): AlbumDto
}