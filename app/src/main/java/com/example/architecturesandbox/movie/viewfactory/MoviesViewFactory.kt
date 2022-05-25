package com.example.architecturesandbox.movie.viewfactory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.architecturesandbox.movie.presentation.ui.fragment.view.MovieDetailView
import com.example.architecturesandbox.movie.presentation.ui.fragment.view.MoviesListView
import com.example.architecturesandbox.movie.presentation.ui.fragment.view.SavedMoviesView
import com.example.architecturesandbox.movie.presentation.ui.fragment.view.SearchItemView
import javax.inject.Inject
import javax.inject.Provider

/**
 * Used to instantiate objects at runtime
 */

class MoviesViewFactory @Inject constructor(
    private val layoutInflaterProvider: Provider<LayoutInflater>,
    private val fragmentManagerProvider: Provider<FragmentManager>
) {

    fun newMoviesListView(parent: ViewGroup?): MoviesListView =
        MoviesListView(layoutInflater = layoutInflaterProvider.get(), parent = parent, fragManager = fragmentManagerProvider.get())

    fun newMovieDetailView(parent: ViewGroup?): MovieDetailView =
        MovieDetailView(layoutInflater = layoutInflaterProvider.get(), parent = parent, fragManager = fragmentManagerProvider.get())

    fun newMoviesSearchView(parent: ViewGroup?): SearchItemView =
        SearchItemView(layoutInflater = layoutInflaterProvider.get(), parent = parent, fragManager = fragmentManagerProvider.get())

    fun newSavedMoviesView(parent: ViewGroup?): SavedMoviesView =
        SavedMoviesView(layoutInflater = layoutInflaterProvider.get(), parent = parent, fragManager = fragmentManagerProvider.get())

}