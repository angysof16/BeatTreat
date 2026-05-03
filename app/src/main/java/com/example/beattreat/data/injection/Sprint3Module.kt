package com.example.beattreat.data.injection

import com.example.beattreat.data.datasource.FollowRemoteDataSource
import com.example.beattreat.data.datasource.FirestoreReviewLiveDataSource
import com.example.beattreat.data.datasource.ReviewLikeRemoteDataSource
import com.example.beattreat.data.datasource.implementation.firestore.FollowFirestoreDataSourceImpl
import com.example.beattreat.data.datasource.implementation.firestore.ReviewLikeFirestoreDataSourceImpl
import com.example.beattreat.data.datasource.implementation.firestore.ReviewLiveFirestoreDataSourceImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para los nuevos data sources del Sprint 3:
 *  - Likes en reviews (subcolecciones + transacciones)
 *  - Seguir usuarios  (subcolecciones + transacciones de 4 pasos)
 *  - Reviews en tiempo real (Flow con snapshot listeners)
 */
@Module
@InstallIn(SingletonComponent::class)
object Sprint3Module {

    @Singleton
    @Provides
    fun provideReviewLikeDataSource(
        db: FirebaseFirestore
    ): ReviewLikeRemoteDataSource = ReviewLikeFirestoreDataSourceImpl(db)

    @Singleton
    @Provides
    fun provideFollowDataSource(
        db: FirebaseFirestore
    ): FollowRemoteDataSource = FollowFirestoreDataSourceImpl(db)

    @Singleton
    @Provides
    fun provideReviewLiveDataSource(
        db: FirebaseFirestore
    ): FirestoreReviewLiveDataSource = ReviewLiveFirestoreDataSourceImpl(db)
}
