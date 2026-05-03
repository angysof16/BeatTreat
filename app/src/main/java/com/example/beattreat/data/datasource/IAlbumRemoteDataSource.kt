package com.example.beattreat.data.datasource

import com.example.beattreat.data.dto.AlbumDto

// fuente de datos remota de albums
interface IAlbumRemoteDataSource {
    suspend fun getAllAlbums(): List<AlbumDto>
    suspend fun getAlbumById(id: Int): AlbumDto
}