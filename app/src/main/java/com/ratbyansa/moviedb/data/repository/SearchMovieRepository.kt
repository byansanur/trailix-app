package com.ratbyansa.moviedb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ratbyansa.moviedb.data.local.dao.SearchHistoryDao
import com.ratbyansa.moviedb.data.local.entity.MovieEntity
import com.ratbyansa.moviedb.data.local.entity.SearchHistoryEntity
import com.ratbyansa.moviedb.data.remote.source.SearchPagingSource
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow

class SearchMovieRepository(
    private val ktorClient: HttpClient,
    private val historyDao: SearchHistoryDao
) {
    fun searchMovies(query: String): Flow<PagingData<MovieEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(ktorClient, query)
            }
        ).flow
    }

    suspend fun saveSearchHistory(query: String) {
        if (query.isNotBlank()) {
            historyDao.insertSearch(SearchHistoryEntity(query))
        }
    }

    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> = historyDao.getRecentSearches()

    suspend fun deleteHistory(query: String) = historyDao.deleteSearch(query)
}