package com.example.login.data.datasource.implementation

import com.example.login.data.datasource.CommentRemoteDataSource
import com.example.login.data.dto.ReviewDto
import com.example.login.data.network.BeatTreatApiService
import javax.inject.Inject

class ReviewRetrofitDataSourceImplementation @Inject constructor(
    private val service: BeatTreatApiService
) : CommentRemoteDataSource {

    override suspend fun getCommentsByAlbum(albumId: Int): List<ReviewDto> =
        service.getReviewsByAlbum(albumId)
}