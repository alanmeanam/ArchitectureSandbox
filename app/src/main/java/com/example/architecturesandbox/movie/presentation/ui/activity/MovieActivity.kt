package com.example.architecturesandbox.movie.presentation.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.architecturesandbox.R
import com.example.architecturesandbox.base.BaseActivity
import com.example.architecturesandbox.databinding.ActivityRootMovieBinding
import com.example.architecturesandbox.movie.presentation.navigator.MovieScreenNavigator
import com.example.architecturesandbox.movie.presentation.ui.fragment.MovieDetailFragment
import com.example.architecturesandbox.movie.presentation.ui.fragment.MoviesListFragment
import com.example.architecturesandbox.movie.presentation.ui.fragment.SearchItemFragment
import com.example.architecturesandbox.movie.presentation.viewmodel.MoviesViewModel
import com.example.architecturesandbox.utils.Constants
import com.example.architecturesandbox.utils.gone
import com.example.architecturesandbox.utils.hideKeyboard
import com.example.architecturesandbox.utils.visible
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MovieActivity : BaseActivity(), MoviesListFragment.TouchActionDelegate, MovieDetailFragment.TouchActionDelegate,
    SearchItemFragment.TouchActionDelegate {

    private lateinit var binding: ActivityRootMovieBinding

    private val toolbar: Toolbar by lazy { binding.toolbarViewLayout.toolbarToolbarView }

    @Inject
    lateinit var screenNavigator: MovieScreenNavigator

    override fun getToolbarInstance(): Toolbar =
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_close_btn)
            title = "Root Activity"
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                backAction()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureTabs()
    }

    override fun setToolbarTitle(title: String) {
        binding.toolbarViewLayout.toolbarTitle.text = String.format(title)
    }

    private fun configureTabs() {
        binding.tabLayout.apply {
            addTab(binding.tabLayout.newTab().setText("Movies"))
            addTab(binding.tabLayout.newTab().setText("Saved Movies"))
            getTabAt(0)
            setToolbarTitle(title = "Latest Movies")
            screenNavigator.toMoviesList(container = R.id.latestFragContainer, backStackName = Constants.moviesListScreen)
            screenNavigator.toSavedFragment(container =  R.id.savedFragContainer, backStackName = Constants.savedScreen)
        }
        setChipsMargins()

        val tabTextView = (((binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(0) as LinearLayout).getChildAt(1) as TextView).apply {
            setTypeface(typeface, Typeface.BOLD)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                for (i in 0 until (binding.tabLayout.getChildAt(0) as ViewGroup).childCount) {
                    val tabTextView2 = ((binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i) as LinearLayout).getChildAt(1) as TextView

                    if (i == tab?.position) {
                        tabTextView2.setTypeface(tabTextView.typeface, Typeface.BOLD)
                        tabTextView2.setTextColor(ContextCompat.getColor(this@MovieActivity, R.color.pumpkin_orange))
                    } else {
                        tabTextView2.typeface = null
                        tabTextView2.setTextColor(ContextCompat.getColor(this@MovieActivity, R.color.auro_metal))
                    }
                }
                if (binding.tabLayout.selectedTabPosition == 0) {
                    setToolbarTitle(title = "Latest Movies")
                    binding.latestFragContainer.visible()
                    binding.savedFragContainer.gone()
                    hideKeyboard()
                } else {
                    setToolbarTitle(title = "Saved Movies")
                    binding.latestFragContainer.gone()
                    binding.savedFragContainer.visible()
                    hideKeyboard()
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun setChipsMargins() {
        (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(0).apply {
            (layoutParams as ViewGroup.MarginLayoutParams).leftMargin = 24
        }
        (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(1).apply {
            (layoutParams as ViewGroup.MarginLayoutParams).leftMargin = 24
        }
    }

    private fun backAction() {
        screenNavigator.activityNavigateBack {
            if(supportFragmentManager.backStackEntryCount == 3)
                binding.tabLayout.visible()
            when(it) {
                "finish" -> finish()
                "onBackPressed" -> supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onBackPressed() {
        backAction()
    }

}