package com.ratbyansa.moviedb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ratbyansa.moviedb.data.local.entity.MovieEntity
import com.ratbyansa.moviedb.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    // State untuk menampung aliran data paging
    private val _moviePagingData = MutableStateFlow<PagingData<MovieEntity>>(PagingData.empty())
    val moviePagingData: StateFlow<PagingData<MovieEntity>> = _moviePagingData

    fun getMoviesByGenre(genreId: Int) {
        viewModelScope.launch {
            repository.getMoviesByGenre(genreId)
                .cachedIn(viewModelScope) // Penting agar data tetap ada saat rotasi layar
                .collect {
                    _moviePagingData.value = it
                }
        }
    }
}