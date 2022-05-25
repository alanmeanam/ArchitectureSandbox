package com.example.architecturesandbox.movie.presentation.ui.fragment.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturesandbox.R
import com.example.architecturesandbox.base.BaseViewComponent
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.presentation.adapter.SearchItemAdapter
import com.example.architecturesandbox.utils.hideKeyboard
import com.example.architecturesandbox.utils.showKeyboard
import com.example.architecturesandbox.utils.visible

class SearchItemView(
    private val layoutInflater: LayoutInflater,
    private val parent: ViewGroup?,
    private val fragManager: FragmentManager
): BaseViewComponent<SearchItemView.Listener>(
    layoutInflater = layoutInflater,
    parent = parent,
    layoutId = R.layout.fragment_search_item
) {

    interface Listener {
        fun onLoadData()
        fun onMovieClicked(clickedMovie: Movie, position: Int)
        fun navigateBack()
    }

    private lateinit var adapter: SearchItemAdapter

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    private var isSearch = false

    init {
        bindViews()
        setAdapter(list = emptyList())
        setSearchView(searchView = searchView)
        searchOptions()
    }

    private fun bindViews() {
        searchView = bindView(id = R.id.searchView)
        recyclerView = bindView(R.id.recyclerView)
    }

    private fun setAdapter(list: List<Movie>) {
        adapter = SearchItemAdapter(movies = list.toMutableList(), callback =  { movie, position ->
            listeners.forEach { it.onMovieClicked(clickedMovie = movie, position = position) }
            parent?.hideKeyboard()
            isSearch = true
            searchView.setQuery("", false)
        }, swipeDelegate = null)
        recyclerView.adapter = this@SearchItemView.adapter
        adapter.swipeSettings(recyclerView = recyclerView, context = context, withAnimation = true)
    }

    fun updateList(list: List<Movie>) {
        adapter.updateList(list = list, returnList = true)
    }

    fun displayLoading(isLoading: Boolean) {
        loadingStatus(fragManager = fragManager, isLoading = isLoading)
    }

    private fun setSearchView(searchView: SearchView) {
        parent?.showKeyboard()
        searchView.visible()
        searchView.isIconified = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.isNotEmpty() as Boolean) {
                    adapter.filter.filter(newText)
                } else {
                    if (isSearch.not())
                        listeners.forEach { it.onLoadData() }
                }
                return false
            }
        })
    }

    private fun searchOptions() {
        searchView.setOnCloseListener {
            listeners.forEach { it.navigateBack() }
            false
        }
    }

}