package com.example.architecturesandbox.viewmodel

import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.data.repository.FakeMoviesRepository
import com.example.architecturesandbox.movie.domain.*
import com.example.architecturesandbox.movie.presentation.viewmodel.MoviesViewModel
import com.example.architecturesandbox.utils.StringUtilImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MoviesViewModelTest {

    private val TEST_API_KEY = "success_apikey"

    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var repository: FakeMoviesRepository
    private lateinit var stringUtil: StringUtilImpl
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    // Make sure viewModelScope uses a test dispatcher
    //@get:Rule
    //val coroutinesDispatcherRule = CoroutineDispatcherRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMoviesRepository()
        stringUtil = StringUtilImpl(gson = Gson())
        moviesViewModel = MoviesViewModel(
            SearchMoviesUseCase(repository, testDispatcher),
            ArrangeInfoUseCase(stringUtil, testDispatcher),
            DeleteFromInsideUseCase(testDispatcher),
            DeleteFromInsideSearchItemUseCase(testDispatcher),
            DeleteMovieUseCase(repository),
            SaveMovieUseCase(repository),
            RefreshSourceListUseCase(),
            RetrieveMoviesUseCase(repository, testDispatcher)
        )
    }

    @Test
    fun fetchMoviesFromRepository_sucess() = testDispatcher.runBlockingTest {
        moviesViewModel.fetchMoviesFlowState(TEST_API_KEY)
        val state = moviesViewModel.moviesList.value
        when(state) {
            is DataState.Success -> {
                println(state.data[0])
                Assert.assertEquals("Matrix", state.data[0].title)
            }
        }
    }

    @Test
    fun fetchMoviesFromRepository_failed() = testDispatcher.runBlockingTest {
        moviesViewModel.fetchMoviesFlowState(TEST_API_KEY)
        val state = moviesViewModel.moviesList.value
        when(state) {
            is DataState.Error -> {
                println(state.error.code)
                Assert.assertEquals(404, state.error.code)
            }
        }
    }

}