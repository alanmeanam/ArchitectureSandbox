package com.example.architecturesandbox.movie.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.architecturesandbox.R
import com.example.architecturesandbox.base.BaseFragment
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.presentation.navigator.MovieScreenNavigator
import com.example.architecturesandbox.movie.presentation.ui.fragment.view.SearchItemView
import com.example.architecturesandbox.movie.presentation.viewmodel.MoviesViewModel
import com.example.architecturesandbox.movie.viewfactory.MoviesViewFactory
import com.example.architecturesandbox.utils.canonicalTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SearchItemFragment: BaseFragment(), SearchItemView.Listener {

    private var touchActionDelegate: TouchActionDelegate? = null

    private var uiJob: Job? = null

    @Inject
    lateinit var screenNavigator: MovieScreenNavigator
    @Inject
    lateinit var viewFactory: MoviesViewFactory

    private val viewModel: MoviesViewModel by activityViewModels()

    private lateinit var viewComp: SearchItemView

    companion object {
        @JvmStatic
        fun newInstance() = SearchItemFragment()
        val TAG = canonicalTag<SearchItemFragment>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            if (it is TouchActionDelegate) touchActionDelegate = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewComp = viewFactory.newMoviesSearchView(parent = container)
        return viewComp.rootView
    }

    override fun onStart() {
        super.onStart()
        touchActionDelegate?.setToolbarTitle(title = "Search Item")
        viewComp.registerListener(this)
        retrieveSearchItems()
    }

    override fun onStop() {
        super.onStop()
        uiJob?.cancelChildren()
        viewComp.unregisterListener(this)
    }

    private fun retrieveSearchItems() {
        uiJob = lifecycleScope.launchWhenResumed {
            viewModel.moviesList.collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> viewComp.displayLoading(isLoading = true)
                    is DataState.Success -> {
                        val lst = dataState.data.filter { it.isHeader.not() }
                        viewComp.displayLoading(isLoading = false)
                        viewComp.updateList(list = lst)
                    }
                    is DataState.Error -> {
                        Log.e("Error", dataState.error.cause)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onLoadData() {
        retrieveSearchItems()
    }

    override fun onMovieClicked(clickedMovie: Movie, position: Int) {
        viewModel.setListPosition(position = position)
        viewModel.setMovieDetailValue(movie = clickedMovie)
        viewModel.setMovieFilteredItem(movie = clickedMovie)
        screenNavigator.toMovieDetails(container = R.id.latestFragContainer, fromSearchView = true)
    }

    override fun navigateBack() {
        screenNavigator.navigateBack()
    }

    interface TouchActionDelegate {
        fun setToolbarTitle(title:String)
    }

}