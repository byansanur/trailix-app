package com.ratbyansa.moviedb.data.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ratbyansa.moviedb.data.local.entity.MovieEntity
import com.ratbyansa.moviedb.data.remote.model.MovieResponse
import com.ratbyansa.moviedb.data.remote.model.toEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class SearchPagingSource(
    private val ktorClient: HttpClient,
    private val query: String
) : PagingSource<Int, MovieEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieEntity> {
        val position = params.key ?: 1
        return try {
            val response = ktorClient.get("search/movie") {
                url {
                    parameters.append("query", query)
                    parameters.append("page", position.toString())
                }
            }.body<MovieResponse>()

            val movies = response.results.map { it.toEntity(genreId = 0) }
            LoadResult.Page(
                data = movies,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}