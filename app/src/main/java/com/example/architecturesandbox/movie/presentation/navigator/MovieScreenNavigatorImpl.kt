package com.example.architecturesandbox.movie.presentation.navigator

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.architecturesandbox.R
import com.example.architecturesandbox.movie.presentation.ui.fragment.MovieDetailFragment
import com.example.architecturesandbox.movie.presentation.ui.fragment.MoviesListFragment
import com.example.architecturesandbox.movie.presentation.ui.fragment.SavedMoviesFragment
import com.example.architecturesandbox.movie.presentation.ui.fragment.SearchItemFragment
import com.example.architecturesandbox.utils.Constants
import com.example.architecturesandbox.utils.gone
import com.example.architecturesandbox.utils.replaceFragmentAnimation
import com.example.architecturesandbox.utils.visible
import javax.inject.Inject

class MovieScreenNavigatorImpl @Inject constructor(
    private val activity: AppCompatActivity,
    private val fragmentManager: FragmentManager
): MovieScreenNavigator {

    override fun activityNavigateBack(callback: (String) -> Unit) {
        val frag = fragmentManager.findFragmentByTag(MoviesListFragment.TAG)
        if (fragmentManager.backStackEntryCount <= 2) callback.invoke("finish")
        //if (frag != null && frag.isVisible) callback.invoke("finish")
        else callback.invoke("onBackPressed")
    }

    override fun navigateBack(showTabs: Boolean, backStackName: String?) {
        if (showTabs)
            activity.findViewById<LinearLayout>(R.id.tabLayout).visible()
        backStackName?.let {
            fragmentManager.popBackStack(it, 0)
        } ?: fragmentManager.popBackStack()
    }

    override fun toMoviesList(container: Int, backStackName: String?) {
        activity.replaceFragmentAnimation(fragment = MoviesListFragment.newInstance(), tag = MoviesListFragment.TAG,
            container = container, backStack = backStackName)
    }

    override fun toSavedFragment(container: Int, backStackName: String?) {
        activity.replaceFragmentAnimation(fragment = SavedMoviesFragment.newInstance(), tag = SavedMoviesFragment.TAG,
            container = container, backStack = backStackName)
    }

    override fun toMovieDetails(container: Int, backStackName: String?, fromSearchView: Boolean) {
        activity.findViewById<LinearLayout>(R.id.tabLayout).gone()
        val fragment = MovieDetailFragment.newInstance()
        val bundle = Bundle().apply {
            putBoolean(Constants.isFromSearchView, fromSearchView)
        }
        fragment.arguments = bundle
        activity.replaceFragmentAnimation(fragment = fragment, tag = MovieDetailFragment.TAG,
            container = container, backStack = backStackName)
    }

    override fun toSearchView(container: Int, name: String?) {
        activity.findViewById<LinearLayout>(R.id.tabLayout).gone()
        activity.replaceFragmentAnimation(fragment = SearchItemFragment.newInstance(), tag = SearchItemFragment.TAG,
            container = container, backStack = name)
    }
}