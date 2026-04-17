// ──────────────────────────────────────────────────────────────────────────────
// FILE: data/injection/FirestoreModule.kt
// ──────────────────────────────────────────────────────────────────────────────
package com.example.login.data.injection

import com.example.login.data.datasource.FirestoreAlbumRemoteDataSource
import com.example.login.data.datasource.FirestoreReviewRemoteDataSource
import com.example.login.data.datasource.FirestoreUserRemoteDataSource
import com.example.login.data.datasource.implementation.firestore.AlbumFirestoreDataSourceImpl
import com.example.login.data.datasource.implementation.firestore.ReviewFirestoreDataSourceImpl
import com.example.login.data.datasource.implementation.firestore.UserFirestoreDataSourceImpl
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
