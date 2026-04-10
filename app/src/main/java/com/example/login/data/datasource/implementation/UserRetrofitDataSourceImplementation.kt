package com.example.login.data.datasource.implementation

import com.example.login.data.datasource.UserRemoteDataSource
import com.example.login.data.dto.UserDto
import com.example.login.data.dto.UserReviewDto
import com.example.login.data.network.BeatTreatApiService
import javax.inject.Inject

/**
 * Implementación de [UserRemoteDataSource] usando Retrofit.
 * Delega directamente al ApiService; el repositorio se encarga
 * de mapear los DTOs y envolver el resultado en Result<T>.
 */
class UserRetrofitDataSourceImplementation @Inject constructor(
    private val service: BeatTreatApiService
) : UserRemoteDataSource {

    override suspend fun getUserById(id: Int): UserDto =
        service.getUserById(id)

    override suspend fun getReviewsByUser(userId: Int): List<UserReviewDto> =
        service.getReviewsByUser(userId)
}
