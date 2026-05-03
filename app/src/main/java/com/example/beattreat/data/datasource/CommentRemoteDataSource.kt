package com.example.beattreat.data.datasource

import com.example.beattreat.data.dto.ReviewDto

interface CommentRemoteDataSource {
    suspend fun getCommentsByAlbum(albumId: Int): List<ReviewDto>
}