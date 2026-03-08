package com.ratbyansa.moviedb.ui.viewmodel

import app.cash.turbine.test
import com.ratbyansa.moviedb.data.local.entity.FavoriteMovieEntity
import com.ratbyansa.moviedb.data.repository.FavoriteRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    private lateinit var viewModel: FavoriteViewModel
    private val repository: FavoriteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch favorite movies`() = runTest(testDispatcher) {
        // Arrange
        val favorites = listOf(
            FavoriteMovieEntity(1, "Movie 1", "path", "back", 8.0, "2023-01-01")
        )
        every { repository.getFavoriteMovies() } returns flowOf(favorites)

        // Act
        viewModel = FavoriteViewModel(repository)
        advanceUntilIdle()

        // Assert
        viewModel.favoriteMovies.test {
            assertEquals(favorites, awaitItem())
        }
    }
}
