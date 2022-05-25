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
import com.example.architecturesandbox.utils.showSimpleDialog

class MoviesListView(
    private val layoutInflater: LayoutInflater,
    private val parent: ViewGroup?,
    private val fragManager: FragmentManager
): BaseViewComponent<MoviesListView.Listener>(
    layoutInflater = layoutInflater,
    parent = parent,
    layoutId = R.layout.fragment_movies_list
), BaseRecyclerAdapter.SwipeActionDelegate {

    interface Listener {
        fun onRefreshClicked()
        fun onMovieClicked(clickedMovie: Movie, position: Int)
        fun onSaveMovie(movie: Movie)
        fun onMovieDeleted(movieDeleted: Movie)
        fun onSearchClicked()
        fun undoDelete(movie: Movie)
        fun setListPosition(position: Int)
        /**
         * when isRefreshAdd == true, means that undo was clicked and the item is added to the data source again,
         * when isRefreshAdd == false, means that the item was deleted and it's removed from the data source
         */
        fun refreshDeletedItems(position: Int, withHeader: Boolean, isRefreshAdd: Boolean, itemsToAdd: List<Movie>)
    }

    private lateinit var adapter: MoviesAdapter

    private lateinit var searchLink: LinearLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var recyclerView: RecyclerView

    private var created = false


    init {
        created = true
        bindViews()
        setAdapter(list = emptyList())
        setSearchLink(searchLink = searchLink)
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
        }, longCallback = { movie ->
            context.showSimpleDialog(message = "Save Movie?",
                listenerPositive = {dialog, _ ->
                    listeners.forEach { it.onSaveMovie(movie = movie)
                        dialog.dismiss()
                    }
                },
                listenerNegative = { dialog, _ -> dialog.dismiss() })
        }, swipeDelegate = this)
        recyclerView.adapter = this@MoviesListView.adapter
        adapter.swipeSettings(recyclerView = recyclerView, context = context, withAnimation = false)
    }

    private fun setSearchLink(searchLink: LinearLayout) {
        searchLink.setOnClickListener {
            listeners.forEach { it.onSearchClicked() }
        }
    }

    fun updateList(list: List<Movie>, position: Int) {
        adapter.updateList(list = list)
        recyclerView.scrollToPosition(position)
        listeners.forEach { it.setListPosition(position = 0) }
    }

    fun displayLoading(isLoading: Boolean) {
        loadingStatus(fragManager = fragManager, isLoading = isLoading)
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

}