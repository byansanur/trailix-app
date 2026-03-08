package com.ratbyansa.moviedb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratbyansa.moviedb.data.remote.model.MovieVideo
import com.ratbyansa.moviedb.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoViewModel(
    private val videoRepository: VideoRepository
) : ViewModel() {
    private val _trailerKey = MutableStateFlow<String?>(null)
    val trailerKey = _trailerKey.asStateFlow()

    private val _videoList = MutableStateFlow<List<MovieVideo>>(emptyList())
    val videoList = _videoList.asStateFlow()

    fun getMovieTrailer(movieId: Long) {
        viewModelScope.launch {
            videoRepository.fetchVideos(movieId).collect { result ->
                result.onSuccess { response ->
                    _videoList.value = response.results
                    // cari 'Trailer' & 'Official' terlebih dahulu
                    val trailer = response.results.find {
                        it.type == "Trailer" && it.official && it.site == "YouTube"
                    } ?: response.results.find {
                        it.type == "Trailer" && it.site == "YouTube"
                    } ?: response.results.firstOrNull {
                        it.site == "YouTube"
                    }

                    _trailerKey.value = trailer?.key
                }.onFailure {
                    _trailerKey.value = null
                }
            }
        }
    }
}