package com.example.architecturesandbox.movie.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturesandbox.base.BaseFilterableAdapter
import com.example.architecturesandbox.common.AppConstants
import com.example.architecturesandbox.databinding.SearchMovieItemBinding
import com.example.architecturesandbox.movie.data.model.Movie
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class SearchItemAdapter (
    private var movies: MutableList<Movie>,
    private val callback: (Movie, Int) -> Unit,
    swipeDelegate: SwipeActionDelegate? = null
) : BaseFilterableAdapter<Movie>(masterList = movies, swipeDelegate = swipeDelegate) {

    private var _itemBinding: SearchMovieItemBinding? = null
    private val itemBinding get() = _itemBinding

    private var filterList = emptyList<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        _itemBinding = SearchMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(bindView = itemBinding!!)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    inner class SearchViewHolder(private val bindView: SearchMovieItemBinding): BaseViewHolder<Movie>(view = bindView.root) {
        override fun onBind(data: Movie, position: Int, isActivated: Boolean) {
            val movie: Movie = data
            Picasso.get().load("${AppConstants.IMAGEURL}${movie.posterPath}").into(bindView.posterView)
            bindView.posterView.setOnClickListener { callback.invoke(movie, position) }
            bindView.movieTitle.text = movie.title
            bindView.movieDate.text = movie.releaseDate
        }
    }

    override fun viewHolderSwipeBehavior(viewHolder: RecyclerView.ViewHolder): Int = ItemTouchHelper.ACTION_STATE_IDLE

    override fun performFiltering(filtrationText: String): ArrayList<Movie> =
        if (filtrationText.isNotBlank())
            filterList.filter {
                it.originalTitle.toLowerCase(Locale.ROOT).contains(filtrationText.toLowerCase(Locale.ROOT))
            } as ArrayList<Movie>
        else
            ArrayList()

    override fun returnUpdatedList(list: List<Movie>) {
        filterList = list
    }

}