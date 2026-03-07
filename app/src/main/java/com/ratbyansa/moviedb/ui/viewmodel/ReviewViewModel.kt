package com.ratbyansa.moviedb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.ratbyansa.moviedb.data.remote.model.Review
import com.ratbyansa.moviedb.data.repository.ReviewRepository
import com.ratbyansa.moviedb.ui.common.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ReviewViewModel(
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    private val _movieId = MutableStateFlow<Long?>(null)
    val movieId: Flow<Long?> = _movieId

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val reviewResult: Flow<PagingData<Review>> = _movieId
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { movieId ->
            if (movieId != null) {
                reviewRepository.fetchReviewByMovieId(movieId)
            } else {
                flowOf(PagingData.empty())
            }
        }

    fun setMovieId(id: Long) {
        _movieId.value = id
    }
}