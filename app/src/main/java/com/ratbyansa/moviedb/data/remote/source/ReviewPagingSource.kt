package com.ratbyansa.moviedb.data.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ratbyansa.moviedb.data.remote.model.Review
import com.ratbyansa.moviedb.data.remote.model.ReviewResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ReviewPagingSource(
    private val ktorClient: HttpClient,
    private val movieId: Long
) : PagingSource<Int, Review>() {
    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        val position = params.key ?: 1
        return try {
            val response = ktorClient.get("movie/$movieId/reviews") {
                url {
                    parameters.append("page", position.toString())
                }
            }.body<ReviewResponse>()
            val review = response.results
            LoadResult.Page(
                data = review,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (review.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}