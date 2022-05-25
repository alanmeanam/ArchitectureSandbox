package com.example.architecturesandbox.movie.presentation.ui.fragment.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturesandbox.R
import com.example.architecturesandbox.base.BaseRecyclerAdapter
import com.example.architecturesandbox.base.BaseViewComponent
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.presentation.adapter.MoviesAdapter
import com.example.architecturesandbox.utils.gone
import com.example.architecturesandbox.utils.showSimpleDialog

class SavedMoviesView(
    private val layoutInflater: LayoutInflater,
    private val parent: ViewGroup?,
    private val fragManager: FragmentManager
): BaseViewComponent<SavedMoviesView.Listener>(
    layoutInflater = layoutInflater,
    parent = parent,
    layoutId = R.layout.fragment_movies_list
), BaseRecyclerAdapter.SwipeActionDelegate {

    interface Listener {
        fun setSavedListPosition(position: Int)
        fun onMovieDeleted(movieDeleted: Movie)
        fun refreshDeletedItems(position: Int, withHeader: Boolean, isRefreshAdd: Boolean, itemsToAdd: List<Movie>)
        fun undoDelete(movie: Movie)
        fun onMovieClicked(clickedMovie: Movie, position: Int)
    }

    private lateinit var adapter: MoviesAdapter

    private lateinit var searchLink: LinearLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var recyclerView: RecyclerView

    init {
        bindViews()
        searchLink.gone()
        setAdapter(list = emptyList())
    }

    private fun bindViews() {
        searchLink = bindView(id = R.id.searchLink)
        recyclerView = bindView(R.id.recyclerView)
        frameLayout = bindView(R.id.frameLayout)
    }

    private fun setAdapter(list: List<Movie>) {
        adapter = MoviesAdapter(movies = list.toMutableList(), callback = { movie, position ->
            listeners.forEach {
                it.onMovieClicked(clickedMovie = movie, position = position)
            }
        },
            longCallback = {},
            swipeDelegate = this)
        recyclerView.adapter = this@SavedMoviesView.adapter
        adapter.swipeSettings(recyclerView = recyclerView, context = context, withAnimation = false)
    }

    override fun <T : Any> swipeDelete(item: T, position: Int) {
        val movie = item as Movie
        val header = adapter.deleteHeader(position = position)
        listeners.forEach { it.onMovieDeleted(movieDeleted = movie) }
        var undoType = BaseRecyclerAdapter.UndoType.SINGLE
        val items = header?.let {
            undoType = BaseRecyclerAdapter.UndoType.HEADER
            listOf(it, movie)
        } ?: listOf(movie)
        listeners.forEach { it.refreshDeletedItems(position = position, withHeader = header != null, isRefreshAdd = false, itemsToAdd = items) }
        adapter.showUndoSnackbar(view = frameLayout as View, item = items, undoType = undoType, position = position,
            message = "Movie deleted",
            actionSingle = {
                listeners.forEach { it.undoDelete(movie = movie) }
                listeners.forEach { it.refreshDeletedItems(position = position, withHeader = header != null,
                    isRefreshAdd = true, itemsToAdd = items) }
            }
        )
    }

    fun updateList(list: List<Movie>, position: Int) {
        adapter.updateList(list = list)
        recyclerView.scrollToPosition(position)
        listeners.forEach { it.setSavedListPosition(position = 0) }
    }

    fun displayLoading(isLoading: Boolean) {
        loadingStatus(fragManager = fragManager, isLoading = isLoading)
    }

}