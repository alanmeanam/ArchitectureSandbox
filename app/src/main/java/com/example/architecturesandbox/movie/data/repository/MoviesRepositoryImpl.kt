package com.example.architecturesandbox.movie.data.repository

import com.example.architecturesandbox.base.BaseError
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.mapper.MovieMapper
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.data.model.MovieEntity
import com.example.architecturesandbox.movie.data.net.MovieService
import com.example.architecturesandbox.realm.RealmBaseContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val service: MovieService,
    private val mapper: MovieMapper,
    private val movieInteractor: RealmBaseContract<MovieEntity>
    ): MoviesRepository {

    /**
     * CLOUD OPERATIONS
     */

    override suspend fun searchMovies(apiKey: String): Flow<DataState<List<Movie>>?> = flow {
        val response = service.searchMovies(api_key = apiKey)
        if (response.isSuccessful) {
            response.body()?.results?.let {
                emit(DataState.Success(data = mapper.mapFromEntityList(entities = it)))
            }
        } else {
            emit(DataState.Error(error = BaseError(code = response.code())))
        }
    }

    /**
     * LOCAL OPERATIONS
     */

    override suspend fun saveMovie(movie: Movie) {
        try {
            val daoModel = mapper.mapToEntity(domainModel = movie)
            movieInteractor.add(obj = daoModel, realmId = movie.id.toLong()).collect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteMovie(id: Long) {
        try {
            movieInteractor.delete(id = id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun retrieveItems(): Flow<DataState<List<Movie>>> = flow {
        try {
            movieInteractor.retrieveAll().collect { state ->
                when(state) {
                    is DataState.Success -> {
                        val movies = mapper.mapFromEntityList(entities = state.data)
                        emit(DataState.Success(data = movies))
                    }
                    is DataState.Error -> emit(state)
                    else -> emit(DataState.Idle)
                }
            }
        } catch (e: Exception) {
            emit(DataState.Error(error = BaseError(exception = e)))
        }
    }

}