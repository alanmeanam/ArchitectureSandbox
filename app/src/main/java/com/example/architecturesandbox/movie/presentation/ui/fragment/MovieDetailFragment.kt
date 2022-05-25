package com.example.architecturesandbox.movie.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.architecturesandbox.R
import com.example.architecturesandbox.base.BaseFragment
import com.example.architecturesandbox.movie.presentation.navigator.MovieScreenNavigator
import com.example.architecturesandbox.movie.presentation.ui.fragment.view.MovieDetailView
import com.example.architecturesandbox.movie.presentation.viewmodel.MoviesViewModel
import com.example.architecturesandbox.movie.viewfactory.MoviesViewFactory
import com.example.architecturesandbox.utils.Constants
import com.example.architecturesandbox.utils.showSimpleDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailFragment: BaseFragment(), MovieDetailView.Listener {

    private var touchActionDelegate: TouchActionDelegate? = null

    private var uiJob: Job? = null

    @Inject
    lateinit var screenNavigator: MovieScreenNavigator
    @Inject
    lateinit var viewFactory: MoviesViewFactory

    private val viewModel: MoviesViewModel by activityViewModels()

    private lateinit var viewComp: MovieDetailView

    private var fromSearchView = false

    companion object {
        @JvmStatic
        fun newInstance() = MovieDetailFragment()
        val TAG = MovieDetailFragment::class.java.canonicalName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fromSearchView = arguments?.getBoolean(Constants.isFromSearchView) ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_inside -> {
                requireContext().showSimpleDialog(message = "¿Delete Movie?",
                    listenerPositive = { dialog, _ ->
                        if (fromSearchView) viewModel.deletedFromInsideSearchView()
                        else viewModel.deleteFromInside()
                        screenNavigator.navigateBack(backStackName = Constants.moviesListScreen)
                        dialog.dismiss()
                    })
            }
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
        viewComp = viewFactory.newMovieDetailView(parent = container)
        return viewComp.rootView
    }

    override fun onStart() {
        super.onStart()
        touchActionDelegate?.setToolbarTitle(title = "Movie Detail Fragment")
        viewComp.displayLoading(isLoading = true)
        viewComp.registerListener(listener = this)
        observeDetails()
    }

    override fun onStop() {
        super.onStop()
        uiJob?.cancelChildren()
        viewComp.unregisterListener(listener = this)
    }

    private fun observeDetails() {
        uiJob = lifecycleScope.launchWhenResumed {
            viewModel.movieDetail.collect { detail ->
                viewComp.updateDetail(detail = detail)
                viewComp.displayLoading(isLoading = false)
            }
        }
    }

    override fun onBtnClicked() {
        screenNavigator.navigateBack(backStackName = Constants.moviesListScreen)
    }

    interface TouchActionDelegate {
        fun setToolbarTitle(title:String)
    }

}