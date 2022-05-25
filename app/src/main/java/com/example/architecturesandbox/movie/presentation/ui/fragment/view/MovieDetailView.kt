package com.example.architecturesandbox.movie.presentation.ui.fragment.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturesandbox.R
import com.example.architecturesandbox.base.BaseViewComponent
import com.example.architecturesandbox.movie.presentation.adapter.MovieDetailAdapter

class MovieDetailView(
    private val layoutInflater: LayoutInflater,
    private val parent: ViewGroup?,
    private val fragManager: FragmentManager
): BaseViewComponent<MovieDetailView.Listener>(
    layoutInflater = layoutInflater,
    parent = parent,
    layoutId = R.layout.fragment_movie_detail
) {

    interface Listener {
        fun onBtnClicked()
    }

    private lateinit var adapter: MovieDetailAdapter

    private lateinit var recyclerView: RecyclerView

    init {
        bindViews()
        setAdapter()
    }

    private fun bindViews() {
        recyclerView = bindView(R.id.recyclerViewDetails)
    }

    private fun setAdapter(list: List<Pair<String, String>> = emptyList()) {
        adapter = MovieDetailAdapter(detail = list)
        recyclerView.adapter = this@MovieDetailView.adapter
    }

    fun updateDetail(detail: List<Pair<String, String>>) {
        adapter.updateList(list = detail)
    }

    fun displayLoading(isLoading: Boolean) {
        loadingStatus(fragManager = fragManager, isLoading = isLoading)
    }

}