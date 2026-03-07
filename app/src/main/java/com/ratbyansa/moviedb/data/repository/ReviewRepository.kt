package com.ratbyansa.moviedb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ratbyansa.moviedb.data.remote.model.Review
import com.ratbyansa.moviedb.data.remote.source.ReviewPagingSource
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow

class ReviewRepository(
    private val ktorClient: HttpClient
) {
    fun fetchReviewByMovieId(movieId: Long): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewPagingSource(ktorClient, movieId)
            }
        ).flow
    }

}