package com.example.architecturesandbox.data.repository

import com.example.architecturesandbox.base.BaseError
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.data.model.MovieEntity
import com.example.architecturesandbox.movie.data.model.MovieResponse
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.hamcrest.CoreMatchers.`is`

class MoviesRepositoryTest {

    private val TEST_API_KEY = "success_apikey"

    private val moviesDetails = MovieResponse().apply {
        this.results = listOf(
            MovieEntity().apply { title = "Matrix" },
            MovieEntity().apply { title = "Doom" }
        )
    }

    //private val testDispatcher = TestCoroutineDispatcher() // StandardTestDispatcher()

    private lateinit var moviesRepository: FakeMoviesRepository

    /*@Test
    fun searchMovies_fetched_success() = runBlocking {
        //GIVEN
        val apiService = mock<MovieService> {
            onBlocking { searchMovies(TEST_API_KEY).body() } doReturn moviesDetails
        }

        val mapper = MovieMapper()

        val movieInteractor = RealmBaseInteractor(MovieEntity::class.java)

        val repository = MoviesRepositoryImpl(service = apiService, mapper = mapper, movieInteractor = movieInteractor)

        val flow = repository.searchMovies(TEST_API_KEY)
        flow.collect { state ->
            when(state) {
                is DataState.Success -> {
                    val data = state.data
                    println(data)
                }
                else -> {}
            }
        }

    }*/

    @Before
    fun setup() {
        moviesRepository = FakeMoviesRepository()
    }

    @Test
    fun searchMovies_argumentsCaptor_success() {
        val ac = ArgumentCaptor.forClass(String::class.java)
        val key = ac.capture()
        //assertEquals(key, apiKey)
    }

    @Test
    fun searchMovies_fetched_success() = runBlocking {
        moviesRepository.searchMovies(apiKey = TEST_API_KEY).collect { state ->
            when(state) {
                is DataState.Success -> {
                    val list = state.data
                    MatcherAssert.assertThat(list[0]::class, `is`(Movie::class))
                    MatcherAssert.assertThat(list[2].title, `is`("Candyman"))
                }
                else -> {}
            }
        }
    }

    @Test
    fun searchMovies_fetched_error() = runBlocking {
        moviesRepository.searchMovies(apiKey = "").collect { state ->
            when(state) {
                is DataState.Error -> {
                    val error = state.error
                    MatcherAssert.assertThat(error::class, `is`(BaseError::class))
                    MatcherAssert.assertThat(error.code, `is`(404))
                }
                else -> {}
            }
        }
    }

    @Test
    fun retrieveMovies_retrieved_success() = runBlocking {
        moviesRepository.retrieveItemsTest(result = 0).collect { state ->
            when(state) {
                is DataState.Success -> {
                    val list = state.data
                    MatcherAssert.assertThat(list[0]::class, `is`(Movie::class))
                    MatcherAssert.assertThat(list[1].voteAverage, `is`(4.2F))
                }
                else -> {}
            }
        }
    }

    @Test
    fun retrieveMovies_retrieved_error() = runBlocking {
        moviesRepository.retrieveItemsTest(result = 1).collect { state ->
            when(state) {
                is DataState.Error -> {
                    val error = state.error
                    MatcherAssert.assertThat(error::class, `is`(BaseError::class))
                    MatcherAssert.assertThat(error.code, `is`(303))
                }
                else -> {}
            }
        }
    }

    @Test
    fun retrieveMovies_retrieved_exception() = runBlocking {
        moviesRepository.retrieveItemsTest(result = 2).collect { state ->
            when(state) {
                is DataState.Error -> {
                    val error = state.error
                    MatcherAssert.assertThat(error::class, `is`(BaseError::class))
                    MatcherAssert.assertThat(error.code, `is`(505))
                }
                else -> {}
            }
        }
    }

}