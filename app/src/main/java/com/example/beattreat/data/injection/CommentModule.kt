package com.example.beattreat.data.injection

import com.example.beattreat.data.datasource.CommentFirestoreDataSource
import com.example.beattreat.data.datasource.implementation.firestore.CommentFirestoreDataSourceImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommentModule {

    @Singleton
    @Provides
    fun provideCommentDataSource(
        db: FirebaseFirestore
    ): CommentFirestoreDataSource = CommentFirestoreDataSourceImpl(db)
}