package com.example.login.data.datasource

import com.example.login.data.dto.ReviewDto

interface CommentRemoteDataSource {
    suspend fun getCommentsByAlbum(albumId: Int): List<ReviewDto>
}