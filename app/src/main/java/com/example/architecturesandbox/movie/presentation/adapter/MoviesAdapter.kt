package com.example.architecturesandbox.movie.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturesandbox.base.BaseRecyclerAdapter
import com.example.architecturesandbox.common.AppConstants
import com.example.architecturesandbox.databinding.ItemDateHeaderBinding
import com.example.architecturesandbox.databinding.MovieItemBinding
import com.example.architecturesandbox.movie.data.model.Movie
import com.squareup.picasso.Picasso

class MoviesAdapter (
    private var movies: MutableList<Movie>,
    private val callback: (Movie, Int) -> Unit,
    private val longCallback: (Movie) -> Unit,
    swipeDelegate: SwipeActionDelegate? = null
): BaseRecyclerAdapter<Movie>(masterList = movies, swipeDelegate = swipeDelegate) {

    interface Listener {

    }

    private var _headerBinding: ItemDateHeaderBinding? = null
    private val headerBinding get() = _headerBinding

    private var _movieBinding: MovieItemBinding? = null
    private val movieBinding get() = _movieBinding

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        val item = movies[position]
        return if (item.isHeader) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 0) {
            _headerBinding = ItemDateHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(bindView = headerBinding!!)
        }
        else {
            _movieBinding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MovieViewHolder(bindView = movieBinding!!)
        }


    inner class HeaderViewHolder(private val bindView: ItemDateHeaderBinding): BaseViewHolder<Movie>(view = bindView.root) {
        override fun onBind(data: Movie, position: Int, isActivated: Boolean) {
            bindView.dateHeader.text = data.releaseDate
        }
    }

    inner class MovieViewHolder(private val bindView: MovieItemBinding): BaseViewHolder<Movie>(view = bindView.root) {
        override fun onBind(data: Movie, position: Int, isActivated: Boolean) {
            val movie: Movie = data
            with(bindView) {
                Picasso.get().load("${AppConstants.IMAGEURL}${movie.posterPath}").into(posterView)
                posterView.setOnClickListener { callback.invoke(movie, position) }
                posterView.setOnLongClickListener {
                    longCallback.invoke(movie)
                    true
                }
                movieTitle.text = movie.title
                Log.e("moviw", "id = ${movie.id}, title = ${movie.title}")
            }
        }
    }

    override fun viewHolderSwipeBehavior(viewHolder: RecyclerView.ViewHolder): Int =
        when (viewHolder) {
            is MovieViewHolder -> ItemTouchHelper.LEFT
            else -> ItemTouchHelper.ACTION_STATE_IDLE
        }

    fun deleteHeader(position: Int): Movie? {
        return try {
            val pos = position -1
            when {
                (pos == itemCount-1 && movies[pos].isHeader) || (movies[pos].isHeader && movies[position].isHeader) -> {
                    val header = movies[pos]
                    recentlyDeleteHeaderSwipe(position = pos)
                    notifyItemRemoved(pos)
                    movies.removeAt(pos)
                    header
                }
                else -> null
            }
        } catch (e: ArrayIndexOutOfBoundsException) { null }
    }

}