package com.ratbyansa.moviedb.ui.viewmodel

import app.cash.turbine.test
import com.ratbyansa.moviedb.data.remote.model.MovieDetailResponse
import com.ratbyansa.moviedb.data.repository.FavoriteRepository
import com.ratbyansa.moviedb.data.repository.MovieRepository
import com.ratbyansa.moviedb.ui.common.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private lateinit var viewModel: MovieDetailViewModel
    private val repository: MovieRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieDetailViewModel(repository, favoriteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMovieDetail should update detailState and isFavorite`() = runTest(testDispatcher) {
        // Arrange
        val movieId = 123L
        val movieDetail = MovieDetailResponse(id = movieId, title = "Test Movie", overview = "")
        coEvery { favoriteRepository.isFavorite(movieId) } returns true
        every { repository.getMovieDetail(movieId) } returns flowOf(Result.success(movieDetail))

        // Act
        viewModel.getMovieDetail(movieId)
        advanceUntilIdle()

        // Assert
        viewModel.detailState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            assertEquals(movieDetail, (state as UiState.Success).data)
        }
        
        viewModel.isFavorite.test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `toggleFavorite should remove from favorites if already favorite`() = runTest(testDispatcher) {
        // Arrange
        val movieId = 123L
        val movieDetail = MovieDetailResponse(id = movieId, title = "Test Movie", overview = "")
        coEvery { favoriteRepository.isFavorite(movieId) } returns true
        every { repository.getMovieDetail(movieId) } returns flowOf(Result.success(movieDetail))
        coEvery { favoriteRepository.removeFromFavorite(movieId) } returns Unit

        viewModel.getMovieDetail(movieId)
        advanceUntilIdle()

        // Act
        viewModel.toggleFavorite(movieDetail)
        advanceUntilIdle()

        // Assert
        coVerify { favoriteRepository.removeFromFavorite(movieId) }
        viewModel.isFavorite.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `toggleFavorite should add to favorites if not already favorite`() = runTest(testDispatcher) {
        // Arrange
        val movieId = 123L
        val movieDetail = MovieDetailResponse(id = movieId, title = "Test Movie", overview = "")
        coEvery { favoriteRepository.isFavorite(movieId) } returns false
        every { repository.getMovieDetail(movieId) } returns flowOf(Result.success(movieDetail))
        coEvery { favoriteRepository.addToFavorite(movieDetail) } returns Unit

        viewModel.getMovieDetail(movieId)
        advanceUntilIdle()

        // Act
        viewModel.toggleFavorite(movieDetail)
        advanceUntilIdle()

        // Assert
        coVerify { favoriteRepository.addToFavorite(movieDetail) }
        viewModel.isFavorite.test {
            assertTrue(awaitItem())
        }
    }
}
