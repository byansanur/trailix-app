package com.ratbyansa.moviedb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratbyansa.moviedb.data.local.entity.GenreEntity
import com.ratbyansa.moviedb.data.repository.MovieRepository
import com.ratbyansa.moviedb.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GenreViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _genreState = MutableStateFlow<UiState<List<GenreEntity>>>(UiState.Loading)
    val genreState: StateFlow<UiState<List<GenreEntity>>> = _genreState

    init {
        getGenres()
    }

    fun getGenres() {
        viewModelScope.launch {
            _genreState.value = UiState.Loading
            repository.getGenres()
                .catch { e ->
                    _genreState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
                }
                .collect { list ->
                    if (list.isEmpty()) _genreState.value = UiState.Empty
                    else _genreState.value = UiState.Success(list)
                }
        }
    }
}