package com.example.login.data.network

import com.example.login.data.dto.AlbumDto
import retrofit2.http.GET
import retrofit2.http.Path

//Interfaz Retrofit que declara los endpoints del backend
interface BeatTreatApiService {

    @GET("albums")
    suspend fun getAllAlbums(): List<AlbumDto>

    @GET("albums/{id}")
    suspend fun getAlbumById(@Path("id") id: Int): AlbumDto
}