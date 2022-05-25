package com.example.architecturesandbox.movie.domain

import com.example.architecturesandbox.common.di.app.DefaultDispatcher
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.data.repository.MoviesRepository
import com.example.architecturesandbox.utils.sortByDateWithHeader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RetrieveMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): Flow<DataState<List<Movie>>> =
        repository.retrieveItems()
            .map { state ->
                when(state) {
                    is DataState.Success -> {
                        val movieList = state.data
                        val sortedList = movieList.sortByDateWithHeader(dateFieldName = "releaseDate", headerFieldName = "isHeader")
                        DataState.Success(data = sortedList)
                    }
                    is DataState.Error -> state
                    else -> state
                }
            }
            .flowOn(defaultDispatcher)

}