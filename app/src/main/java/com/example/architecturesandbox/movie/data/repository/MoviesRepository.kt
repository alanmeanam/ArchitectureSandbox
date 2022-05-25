package com.example.architecturesandbox.movie.data.repository

import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {

    /**
     * CLOUD OPERATIONS
     */

    suspend fun searchMovies(apiKey: String): Flow<DataState<List<Movie>>?>

    /**
     * LOCAL OPERATIONS
     */

    suspend fun saveMovie(movie: Movie)
    suspend fun deleteMovie(id: Long)
    suspend fun retrieveItems(): Flow<DataState<List<Movie>>>

}