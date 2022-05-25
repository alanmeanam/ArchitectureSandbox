package com.example.architecturesandbox.data.repository

import com.example.architecturesandbox.base.BaseError
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.mapper.MovieMapper
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.data.model.MovieEntity
import com.example.architecturesandbox.movie.data.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMoviesRepository: MoviesRepository {

    private val TEST_API_KEY = "success_apikey"
    private val mapper = MovieMapper()

    private val movieEntityList = listOf(
        MovieEntity().apply {
            id = 0
            releaseDate = "2021-03-03"
            title = "Matrix"
            voteAverage = 4.1F
            voteCount = 5.1F
        },
        MovieEntity().apply {
            id = 1
            releaseDate = "2019-01-01"
            title = "Inception"
            voteAverage = 4.2F
            voteCount = 5.2F
        },
        MovieEntity().apply {
            id = 2
            releaseDate = "2020-02-02"
            title = "Candyman"
            voteAverage = 4.3F
            voteCount = 5.3F
        }
    )

    override suspend fun searchMovies(apiKey: String): Flow<DataState<List<Movie>>?> = flow {
        try {
            val response = apiKey == "954703de0f569a4d5ecfccefce72d44e" //TEST_API_KEY
            if (response) {
                emit(DataState.Success(data = mapper.mapFromEntityList(entities = movieEntityList)))
            } else {
                emit(DataState.Error(error = BaseError(code = 404, cause = "Response Failed")))
            }
        } catch (e: Exception) {
            //Emissions from 'catch' blocks are prohibited in order to avoid unspecified behaviour, 'Flow.catch' operator can be used instead.
            e.printStackTrace()
        }
    }

    override suspend fun saveMovie(movie: Movie) {}


    override suspend fun deleteMovie(id: Long) {}

    override suspend fun retrieveItems(): Flow<DataState<List<Movie>>> = flow {}

    suspend fun retrieveItemsTest(result: Int): Flow<DataState<List<Movie>>> = flow {
        try {
            val state = when(result) {
                0 -> DataState.Success(data = movieEntityList)
                1 -> DataState.Error(error = BaseError(code = 303))
                2 -> throw Exception()
                else -> DataState.Idle
            }
            when(state) {
                is DataState.Success -> {
                    val movies = mapper.mapFromEntityList(entities = state.data)
                    emit(DataState.Success(data = movies))
                }
                is DataState.Error -> emit(state)
                else -> {}
            }
        } catch (e: Exception) {
            emit(DataState.Error(error = BaseError(code = 505, exception = e)))
        }
    }

}