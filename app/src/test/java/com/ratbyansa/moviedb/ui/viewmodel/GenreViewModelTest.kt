package com.ratbyansa.moviedb.ui.viewmodel

import app.cash.turbine.test
import com.ratbyansa.moviedb.data.local.entity.GenreEntity
import com.ratbyansa.moviedb.data.repository.MovieRepository
import com.ratbyansa.moviedb.ui.common.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenreViewModelTest {

    private lateinit var viewModel: GenreViewModel
    private val repository: MovieRepository = mockk()
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
    fun `getGenres should emit Success when repository returns data`() = runTest(testDispatcher) {
        // Arrange
        val genres = listOf(GenreEntity(1, "Action"))
        coEvery { repository.getGenres() } returns flowOf(genres)

        // Act
        viewModel = GenreViewModel(repository)

        // Assert
        viewModel.genreState.test {
            val state = awaitItem()
            if (state is UiState.Loading) {
                val nextState = awaitItem()
                assertTrue(nextState is UiState.Success)
                assertEquals(genres, (nextState as UiState.Success).data)
            } else {
                assertTrue(state is UiState.Success)
                assertEquals(genres, (state as UiState.Success).data)
            }
        }
    }

    @Test
    fun `getGenres should emit Empty when repository returns empty list`() = runTest(testDispatcher) {
        // Arrange
        coEvery { repository.getGenres() } returns flowOf(emptyList())

        // Act
        viewModel = GenreViewModel(repository)

        // Assert
        viewModel.genreState.test {
            val state = awaitItem()
            if (state is UiState.Loading) {
                val nextState = awaitItem()
                assertEquals(UiState.Empty, nextState)
            } else {
                assertEquals(UiState.Empty, state)
            }
        }
    }

    @Test
    fun `getGenres should emit Error when repository throws exception`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "Network Error"
        coEvery { repository.getGenres() } returns flow { throw Exception(errorMessage) }

        // Act
        viewModel = GenreViewModel(repository)

        // Assert
        viewModel.genreState.test {
            val state = awaitItem()
            if (state is UiState.Loading) {
                val nextState = awaitItem()
                assertTrue(nextState is UiState.Error)
                assertEquals(errorMessage, (nextState as UiState.Error).message)
            } else {
                assertTrue(state is UiState.Error)
                assertEquals(errorMessage, (state as UiState.Error).message)
            }
        }
    }
}
