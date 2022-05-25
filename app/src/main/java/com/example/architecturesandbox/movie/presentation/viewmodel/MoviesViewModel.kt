package com.example.architecturesandbox.movie.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architecturesandbox.BuildConfig
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val arrangeInfoUseCase: ArrangeInfoUseCase,
    private val deleteFromInsideUseCase: DeleteFromInsideUseCase,
    private val deleteFromInsideSearchItemUseCase: DeleteFromInsideSearchItemUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val saveMovieUseCase: SaveMovieUseCase,
    private val refreshSourceListUseCase: RefreshSourceListUseCase,
    private val retrieveMoviesUseCase: RetrieveMoviesUseCase
    ): ViewModel() {

    private val _moviesList = MutableStateFlow<DataState<List<Movie>>>(DataState.Loading)
    val moviesList: StateFlow<DataState<List<Movie>>> get() = _moviesList

    private val _savedMoviesList = MutableStateFlow<DataState<List<Movie>>>(DataState.Loading)
    val savedMoviesList: StateFlow<DataState<List<Movie>>> get() = _savedMoviesList

    private val _movieDetail = MutableStateFlow<List<Pair<String, String>>>(listOf(Pair("", "")))
    val movieDetail: StateFlow<List<Pair<String, String>>> get() = _movieDetail

    private val _movieFilteredItem = MutableStateFlow<Movie?>(null)
    val movieFilteredItem: StateFlow<Movie?> get() = _movieFilteredItem

    private val _listPosition = MutableStateFlow(0)
    val listPosition: StateFlow<Int> get() = _listPosition

    private val _savedListPosition = MutableStateFlow(0)
    val savedListPosition: StateFlow<Int> get() = _savedListPosition

    init {
        fetchMoviesFlowState()
        retrieveMoviesFlowState()
    }

    fun fetchMoviesFlowState(apiKey: String = BuildConfig.MOVIE_API_KEY) {
        viewModelScope.launch {
            searchMoviesUseCase(apiKey = apiKey).collect { list ->
                list?.let {
                    _moviesList.value = it
                }
            }
        }
    }

    private fun retrieveMoviesFlowState() {
        viewModelScope.launch {
            retrieveMoviesUseCase().collect {
                _savedMoviesList.value = it
            }
        }
    }

    fun setMovieDetailValue(movie: Movie) {
        viewModelScope.launch {
            arrangeInfoUseCase(movie = movie).collect { detailsList ->
                _movieDetail.value = detailsList
            }
        }
    }

    fun deleteFromInside() {
        viewModelScope.launch {
            deleteFromInsideUseCase(dataSource = moviesList.value, position = listPosition.value).collect {
                _moviesList.value = it
            }
        }
    }

    fun deletedFromInsideSearchView() {
        viewModelScope.launch {
            movieFilteredItem.value?.let { mov ->
                deleteFromInsideSearchItemUseCase(dataSource = moviesList.value, itemToDelete = mov).collect {
                    _moviesList.value = it
                }
            }
        }
    }

    fun saveMovie(movie: Movie) {
        viewModelScope.launch {
            saveMovieUseCase(movie = movie)
        }
    }

    fun deleteMovie(id: Long) {
        viewModelScope.launch {
            deleteMovieUseCase(id = id)
        }
    }

    fun refreshDeletedItems(refreshType: RefreshType, position: Int, itemsToAdd: List<Movie>) {
        _listPosition.value = position
        viewModelScope.launch {
            refreshSourceListUseCase(dataSource = moviesList.value, refreshType = refreshType,
                position = position, itemsToAdd = itemsToAdd).collect {
                _moviesList.value = it
            }
        }
    }

    fun refreshDeletedSavedItems(refreshType: RefreshType, position: Int, itemsToAdd: List<Movie>) {
        _listPosition.value = position
        viewModelScope.launch {
            refreshSourceListUseCase(dataSource = savedMoviesList.value, refreshType = refreshType,
                position = position, itemsToAdd = itemsToAdd).collect {
                _savedMoviesList.value = it
            }
        }
    }

    fun setListPosition(position: Int) {
        _listPosition.value = position
    }

    fun setSavedListPosition(position: Int) {
        _savedListPosition.value = position
    }

    fun setMovieFilteredItem(movie: Movie) {
        _movieFilteredItem.value = movie
    }

    enum class RefreshType {
        ADD_SINGLE,
        ADD_WITH_HEADER,
        DELETE_SINGLE,
        DELETE_WITH_HEADER
    }

}