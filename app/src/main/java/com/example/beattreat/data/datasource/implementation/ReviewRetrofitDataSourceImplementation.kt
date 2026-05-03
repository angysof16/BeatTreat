package com.example.beattreat.data.datasource.implementation

import com.example.beattreat.data.datasource.CommentRemoteDataSource
import com.example.beattreat.data.dto.ReviewDto
import com.example.beattreat.data.network.BeatTreatApiService
import javax.inject.Inject

class ReviewRetrofitDataSourceImplementation @Inject constructor(
    private val service: BeatTreatApiService
) : CommentRemoteDataSource {

    override suspend fun getCommentsByAlbum(albumId: Int): List<ReviewDto> =
        service.getReviewsByAlbum(albumId)
}