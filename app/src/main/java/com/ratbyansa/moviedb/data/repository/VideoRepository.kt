package com.ratbyansa.moviedb.data.repository

import com.ratbyansa.moviedb.data.local.entity.GenreEntity
import com.ratbyansa.moviedb.data.remote.model.GenreListResponse
import com.ratbyansa.moviedb.data.remote.model.MovieDetailResponse
import com.ratbyansa.moviedb.data.remote.model.MovieVideoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VideoRepository(private val ktorClient: HttpClient) {

    fun fetchVideos(movieId: Long): Flow<Result<MovieVideoResponse>> = flow {
        try {
            val response = ktorClient
                .get("movie/$movieId/videos")
                .body<MovieVideoResponse>()
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}