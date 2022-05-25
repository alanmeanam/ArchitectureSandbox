package com.example.architecturesandbox.movie.domain

import com.example.architecturesandbox.base.BaseError
import com.example.architecturesandbox.common.ThreadInfoLogger
import com.example.architecturesandbox.common.di.app.IoDispatcher
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.data.repository.MoviesRepository
import com.example.architecturesandbox.utils.sortByDateWithHeader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(apiKey: String): Flow<DataState<List<Movie>>?> =
        repository.searchMovies(apiKey = apiKey)
            .catch { e ->
                e.printStackTrace()
                emit(DataState.Error(error = BaseError(code = 666)))
            }
            .map { state ->
                ThreadInfoLogger.printThreadInfo(message = "SearchMoviesUseCase flow map")
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
            .flowOn(ioDispatcher)

}