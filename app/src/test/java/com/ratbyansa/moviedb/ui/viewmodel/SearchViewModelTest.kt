package com.ratbyansa.moviedb.ui.viewmodel

import androidx.paging.PagingData
import app.cash.turbine.test
import com.ratbyansa.moviedb.data.local.entity.MovieEntity
import com.ratbyansa.moviedb.data.repository.SearchMovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val repository: SearchMovieRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getSearchHistory() } returns flowOf(emptyList())
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChanged should update searchQuery`() = runTest(testDispatcher) {
        val query = "Action"
        viewModel.onSearchQueryChanged(query)
        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun `searchResult should emit empty PagingData when query length less than 3`() = runTest(testDispatcher) {
        viewModel.onSearchQueryChanged("ab")
        
        viewModel.searchResult.test {
            advanceTimeBy(501)
            awaitItem() // Empty paging data
        }
    }

    @Test
    fun `searchResult should fetch from repository when query length is 3 or more`() = runTest(testDispatcher) {
        val query = "Batman"
        val pagingData = PagingData.empty<MovieEntity>()
        every { repository.searchMovies(query) } returns flowOf(pagingData)
        coEvery { repository.saveSearchHistory(query) } returns Unit

        viewModel.onSearchQueryChanged(query)

        viewModel.searchResult.test {
            advanceTimeBy(501)
            awaitItem()
            coVerify { repository.saveSearchHistory(query) }
            coVerify { repository.searchMovies(query) }
        }
    }

    @Test
    fun `deleteHistory should call repository delete`() = runTest(testDispatcher) {
        val query = "History"
        coEvery { repository.deleteHistory(query) } returns Unit
        
        viewModel.deleteHistory(query)
        advanceTimeBy(1)
        
        coVerify { repository.deleteHistory(query) }
    }
}
