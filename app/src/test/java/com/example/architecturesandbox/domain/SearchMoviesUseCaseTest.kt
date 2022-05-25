package com.example.architecturesandbox.domain

import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.data.repository.FakeMoviesRepository
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.domain.SearchMoviesUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.hamcrest.MatcherAssert
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchMoviesUseCaseTest {

    private val TEST_API_KEY = "success_apikey"

    private val testDispatcher = TestCoroutineDispatcher() // StandardTestDispatcher()
    private lateinit var moviesRepository: FakeMoviesRepository
    private lateinit var useCase: SearchMoviesUseCase

    @Before
    fun setup() {
        moviesRepository = FakeMoviesRepository()
        useCase = SearchMoviesUseCase(repository = moviesRepository, ioDispatcher = testDispatcher)
    }


    @Test
    fun searchMovies_sorted_success() = runBlocking {
        useCase(apiKey = TEST_API_KEY).collect { state ->
            when(state) {
                is DataState.Success -> {
                    val list = state.data
                    println(list.size)
                    MatcherAssert.assertThat(list[0]::class, `is`(Movie::class))
                    Assert.assertTrue(list[0].isHeader)
                    MatcherAssert.assertThat(list.size, `is`(6))
                    MatcherAssert.assertThat(list[1].id, `is`(0))
                }
                else -> {}
            }
        }
    }

    @Test
    fun searchMovies_sorted_failed() = runBlocking {
        useCase(apiKey = "other api key").collect { state ->
            when(state) {
                is DataState.Error -> {
                    val error = state.error
                    println(error.cause)
                    MatcherAssert.assertThat(error.code, `is`(404))
                }
                else -> {}
            }
        }
    }

}