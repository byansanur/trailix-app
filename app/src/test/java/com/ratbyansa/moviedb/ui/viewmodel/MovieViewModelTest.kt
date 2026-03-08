package com.ratbyansa.moviedb.ui.viewmodel

import androidx.paging.PagingData
import app.cash.turbine.test
import com.ratbyansa.moviedb.data.local.entity.MovieEntity
import com.ratbyansa.moviedb.data.repository.MovieRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {

    private lateinit var viewModel: MovieViewModel
    private val repository: MovieRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `moviePagingData should fetch from repository when genre is set`() = runTest {
        // Arrange
        val genreId = 1L
        val pagingData = PagingData.empty<MovieEntity>()
        every { repository.getMoviesByGenre(genreId) } returns flowOf(pagingData)

        // Act
        viewModel.setGenre(genreId)

        // Assert
        viewModel.moviePagingData.test {
            awaitItem() // PagingData is emitted
            verify { repository.getMoviesByGenre(genreId) }
        }
    }
}
