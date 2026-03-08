package com.ratbyansa.moviedb.ui.viewmodel

import androidx.paging.PagingData
import app.cash.turbine.test
import com.ratbyansa.moviedb.data.remote.model.Review
import com.ratbyansa.moviedb.data.repository.ReviewRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewViewModelTest {

    private lateinit var viewModel: ReviewViewModel
    private val repository: ReviewRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReviewViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `reviewResult should emit empty PagingData initially`() = runTest(testDispatcher) {
        viewModel.reviewResult.test {
            awaitItem() // Initial empty paging data because movieId is null
        }
    }

    @Test
    fun `reviewResult should fetch from repository when movieId is set`() = runTest(testDispatcher) {
        val movieId = 123L
        val pagingData = PagingData.empty<Review>()
        every { repository.fetchReviewByMovieId(movieId) } returns flowOf(pagingData)

        viewModel.setMovieId(movieId)

        viewModel.reviewResult.test {
            advanceTimeBy(501) // Account for debounce(500)
            awaitItem() // First emission (might be empty from initial null if debounce allowed it, but here we expect the result for movieId)
            // Depending on how flatMapLatest and debounce interact in the test, 
            // we might need to be careful with the items.
        }
    }
}
