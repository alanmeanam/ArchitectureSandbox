package com.example.architecturesandbox.movie.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.architecturesandbox.R
import com.example.architecturesandbox.base.BaseFragment
import com.example.architecturesandbox.common.state.DataState
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.presentation.navigator.MovieScreenNavigator
import com.example.architecturesandbox.movie.presentation.ui.fragment.view.SavedMoviesView
import com.example.architecturesandbox.movie.presentation.viewmodel.MoviesViewModel
import com.example.architecturesandbox.movie.viewfactory.MoviesViewFactory
import com.example.architecturesandbox.utils.canonicalTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

@AndroidEntryPoint
class SavedMoviesFragment: BaseFragment(), SavedMoviesView.Listener {

    private var touchActionDelegate: TouchActionDelegate? = null

    private var uiJob: Job? = null

    @Inject
    lateinit var screenNavigator: MovieScreenNavigator
    @Inject
    lateinit var viewFactory: MoviesViewFactory

    private val viewModel: MoviesViewModel by activityViewModels()

    private lateinit var viewComp: SavedMoviesView

    private var listPosition = 0


    companion object {
        @JvmStatic
        fun newInstance() = SavedMoviesFragment()
        val TAG = canonicalTag<SavedMoviesFragment>()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.lobby, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {}
            R.id.action_loading -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            if(it is TouchActionDelegate) touchActionDelegate = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        viewComp = viewFactory.newSavedMoviesView(parent = container)
        return viewComp.rootView
    }

    override fun onStart() {
        super.onStart()
        viewComp.registerListener(listener = this)
        retrieveSavedMovies()
    }

    override fun onStop() {
        super.onStop()
        uiJob?.cancelChildren()
        viewComp.unregisterListener(listener = this)
    }

    private fun retrieveSavedMovies() {
        uiJob = lifecycleScope.launchWhenResumed {
            viewModel.savedMoviesList.collect { state ->
                when(state) {
                    is DataState.Loading -> viewComp.displayLoading(isLoading = true)
                    is DataState.Success -> {
                        viewComp.displayLoading(isLoading = false)
                        val lst = state.data
                        viewComp.updateList(list = lst, position = listPosition)
                    }
                    is DataState.Error -> {
                        viewComp.displayLoading(isLoading = false)
                        displayError(message = "Error: ${state.error.cause},")
                    }
                    is DataState.Idle -> {}
                }
            }
        }
    }

    override fun refreshDeletedItems(position: Int, withHeader: Boolean, isRefreshAdd: Boolean, itemsToAdd: List<Movie>) {
        val refreshType =
            when {
                isRefreshAdd && withHeader.not() -> MoviesViewModel.RefreshType.ADD_SINGLE
                isRefreshAdd && withHeader -> MoviesViewModel.RefreshType.ADD_WITH_HEADER
                isRefreshAdd.not() && withHeader.not() -> MoviesViewModel.RefreshType.DELETE_SINGLE
                isRefreshAdd.not() && withHeader -> MoviesViewModel.RefreshType.DELETE_WITH_HEADER
                else -> throw UnsupportedOperationException("Type not yet supported")
            }
        viewModel.refreshDeletedSavedItems(refreshType = refreshType, position = position, itemsToAdd = itemsToAdd)
    }

    override fun onMovieClicked(clickedMovie: Movie, position: Int) {
        viewModel.setSavedListPosition(position = position)
        viewModel.setMovieDetailValue(movie = clickedMovie)
        screenNavigator.toMovieDetails(container = R.id.savedFragContainer)
    }

    override fun onMovieDeleted(movieDeleted: Movie) {
        viewModel.deleteMovie(id = movieDeleted.id.toLong())
    }

    override fun undoDelete(movie: Movie) {
        viewModel.saveMovie(movie = movie)
    }

    private fun displayError(message: String?) {
        message?.let {
            toast(text = message)
        } ?: toast(text = "Unknown error")
    }

    override fun setSavedListPosition(position: Int) {
        viewModel.setSavedListPosition(position = position)
    }

    interface TouchActionDelegate {
        fun setToolbarTitle(title:String)
    }

}