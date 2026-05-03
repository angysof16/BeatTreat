package com.example.beattreat.data.datasource.implementation

import com.example.beattreat.data.datasource.AlbumRemoteDataSource
import com.example.beattreat.data.dto.AlbumDto
import com.example.beattreat.data.network.BeatTreatApiService
import javax.inject.Inject

// Implementación del data source usando Retrofit
class AlbumRetrofitDataSourceImplementation @Inject constructor(
    private val service: BeatTreatApiService
) : AlbumRemoteDataSource {

    override suspend fun getAllAlbums(): List<AlbumDto> =
        service.getAllAlbums()

    override suspend fun getAlbumById(id: Int): AlbumDto =
        service.getAlbumById(id)
}