package com.example.login.data.datasource

import com.example.login.data.dto.AlbumDto

// Contrato del data source de álbumes
interface AlbumRemoteDataSource {
    suspend fun getAllAlbums(): List<AlbumDto>
    suspend fun getAlbumById(id: Int): AlbumDto
}