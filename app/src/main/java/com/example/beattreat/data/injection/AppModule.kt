package com.example.beattreat.data.injection

import com.example.beattreat.data.network.BeatTreatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Módulo Hilt que provee la instancia de Retrofit y el ApiService
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Crea la instancia singleton de Retrofit
    // 10.0.2.2
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Crea la instancia del ApiService a partir de Retrofit
    @Singleton
    @Provides
    fun provideBeatTreatApiService(retrofit: Retrofit): BeatTreatApiService {
        return retrofit.create(BeatTreatApiService::class.java)
    }
}