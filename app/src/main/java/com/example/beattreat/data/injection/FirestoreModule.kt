// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/injection/FirestoreModule.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.beattreat.data.injection

import com.example.beattreat.data.datasource.FirestoreAlbumRemoteDataSource
import com.example.beattreat.data.datasource.FirestoreReviewRemoteDataSource
import com.example.beattreat.data.datasource.FirestoreUserRemoteDataSource
import com.example.beattreat.data.datasource.implementation.firestore.AlbumFirestoreDataSourceImpl
import com.example.beattreat.data.datasource.implementation.firestore.ReviewFirestoreDataSourceImpl
import com.example.beattreat.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    @Singleton
    @Provides
    fun provideUserFirestoreDataSource(
        db: FirebaseFirestore
    ): FirestoreUserRemoteDataSource = UserFirestoreDataSourceImpl(db)

    @Singleton
    @Provides
    fun provideAlbumFirestoreDataSource(
        db: FirebaseFirestore
    ): FirestoreAlbumRemoteDataSource = AlbumFirestoreDataSourceImpl(db)

    @Singleton
    @Provides
    fun provideReviewFirestoreDataSource(
        db: FirebaseFirestore
    ): FirestoreReviewRemoteDataSource = ReviewFirestoreDataSourceImpl(db)
}
