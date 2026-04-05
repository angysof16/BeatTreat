package com.example.login.data.network

import com.example.login.data.dto.AlbumDto
import com.example.login.data.dto.UserDto
import com.example.login.data.dto.UserReviewDto
import retrofit2.http.GET
import retrofit2.http.Path

//Interfaz Retrofit que declara los endpoints del backend
interface BeatTreatApiService {

    @GET("albums")
    suspend fun getAllAlbums(): List<AlbumDto>

    @GET("albums/{id}")
    suspend fun getAlbumById(@Path("id") id: Int): AlbumDto

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto

    @GET("users/{userId}/reviews")
    suspend fun getReviewsByUser(@Path("userId") userId: Int): List<UserReviewDto>
}