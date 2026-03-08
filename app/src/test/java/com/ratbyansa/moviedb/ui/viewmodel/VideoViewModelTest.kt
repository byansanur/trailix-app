package com.ratbyansa.moviedb.ui.viewmodel

import app.cash.turbine.test
import com.ratbyansa.moviedb.data.remote.model.MovieVideo
import com.ratbyansa.moviedb.data.remote.model.MovieVideoResponse
import com.ratbyansa.moviedb.data.repository.VideoRepository
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoViewModelTest {

    private lateinit var viewModel: VideoViewModel
    private val repository: VideoRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = VideoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMovieTrailer should update trailerKey with official trailer if available`() = runTest(testDispatcher) {
        // Arrange
        val movieId = 123L
        val videos = listOf(
            MovieVideo(
                id = "1", 
                key = "key1", 
                name = "Teaser", 
                site = "YouTube", 
                type = "Teaser", 
                official = true, 
                publishedAt = "2023-01-01"
            ),
            MovieVideo(
                id = "2", 
                key = "key2", 
                name = "Official Trailer", 
                site = "YouTube", 
                type = "Trailer", 
                official = true, 
                publishedAt = "2023-01-02"
            )
        )
        val response = MovieVideoResponse(id = movieId.toInt(), results = videos)
        every { repository.fetchVideos(movieId) } returns flowOf(Result.success(response))

        // Act
        viewModel.getMovieTrailer(movieId)
        advanceUntilIdle()

        // Assert
        viewModel.trailerKey.test {
            assertEquals("key2", awaitItem())
        }
        viewModel.videoList.test {
            assertEquals(videos, awaitItem())
        }
    }

    @Test
    fun `getMovieTrailer should set trailerKey to null on failure`() = runTest(testDispatcher) {
        // Arrange
        val movieId = 123L
        every { repository.fetchVideos(movieId) } returns flowOf(Result.failure(Exception("Error")))

        // Act
        viewModel.getMovieTrailer(movieId)
        advanceUntilIdle()

        // Assert
        viewModel.trailerKey.test {
            assertNull(awaitItem())
        }
    }
}
