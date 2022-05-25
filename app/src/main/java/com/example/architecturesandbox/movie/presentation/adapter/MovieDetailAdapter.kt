package com.example.architecturesandbox.movie.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturesandbox.base.BaseRecyclerAdapter
import com.example.architecturesandbox.databinding.MovieItemBinding
import com.example.architecturesandbox.utils.gone

class MovieDetailAdapter(detail: List<Pair<String, String>>): BaseRecyclerAdapter<Pair<String, String>>(masterList = detail.toMutableList()) {

    private var _binding: MovieItemBinding? = null
    private val binding get() = _binding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        _binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieDetailViewHolder(bindView = binding as MovieItemBinding)
    }

    inner class MovieDetailViewHolder(private val bindView: MovieItemBinding): BaseViewHolder<Pair<String, String>>(view = bindView.root) {
        override fun onBind(data: Pair<String, String>, position: Int, isActivated: Boolean) {
            val movieDetail: Pair<String, String> = data
            with(view) {
                bindView.posterView.gone()
                bindView.movieTitle.text = String.format("${movieDetail.first} - ${movieDetail.second}")
            }
        }
    }

    override fun viewHolderSwipeBehavior(viewHolder: RecyclerView.ViewHolder): Int = ItemTouchHelper.ACTION_STATE_IDLE

}