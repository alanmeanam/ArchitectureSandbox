package com.example.architecturesandbox.movie.domain

import com.example.architecturesandbox.common.ThreadInfoLogger
import com.example.architecturesandbox.common.di.app.DefaultDispatcher
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteFromInsideSearchItemUseCase @Inject constructor(@DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(
        dataSource: DataState<List<Movie>>,
        itemToDelete: Movie
    ): Flow<DataState<List<Movie>>> = flow {
        ThreadInfoLogger.printThreadInfo(message = "DeleteFromInsideSearchItemUseCase")
        val lst = when(dataSource) {
            is DataState.Success -> {
                dataSource.data.ifEmpty { emptyList() }
            }
            else -> emptyList()
        }.toMutableList()
        val position = lst.indexOf(itemToDelete)
        when {
            position == lst.size-1 && lst[position-1].isHeader -> {
                val items = listOf(lst[position-1], lst[position])
                lst.removeAll(items)
            }
            position != lst.size-1 && lst[position-1].isHeader && lst[position+1].isHeader.not() -> lst.removeAt(position)
            lst[position-1].isHeader -> {
                val items = listOf(lst[position-1], lst[position])
                lst.removeAll(items)
            }
            else -> lst.removeAt(position)
        }
        emit(DataState.Success(data = lst.toList()))
    }.flowOn(defaultDispatcher)

}