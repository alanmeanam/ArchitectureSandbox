package com.example.architecturesandbox.movie.domain

import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.presentation.viewmodel.MoviesViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RefreshSourceListUseCase @Inject constructor() {

    suspend operator fun invoke(
        dataSource: DataState<List<Movie>>,
        refreshType: MoviesViewModel.RefreshType,
        position: Int,
        itemsToAdd: List<Movie>
    ): Flow<DataState<List<Movie>>> = flow {
        val lst = when(dataSource) {
            is DataState.Success -> {
                if (dataSource.data.isEmpty()) emptyList()
                else dataSource.data
            }
            else -> emptyList()
        }.toMutableList()
        when(refreshType) {
            MoviesViewModel.RefreshType.ADD_SINGLE -> lst.add(position, itemsToAdd.single())
            MoviesViewModel.RefreshType.ADD_WITH_HEADER -> {
                lst.addAll(position-1, itemsToAdd)
            }
            MoviesViewModel.RefreshType.DELETE_SINGLE -> lst.removeAt(position)
            MoviesViewModel.RefreshType.DELETE_WITH_HEADER -> {
                val items = listOf(lst[position-1], lst[position])
                lst.removeAll(items)
            }
        }
        emit(DataState.Success(data = lst.toList()))
    }

}