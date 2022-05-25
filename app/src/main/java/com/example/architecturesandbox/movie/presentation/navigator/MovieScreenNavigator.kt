package com.example.architecturesandbox.movie.presentation.navigator

import androidx.annotation.IdRes

interface MovieScreenNavigator {

    fun activityNavigateBack(callback: (String) -> Unit)
    fun navigateBack(showTabs: Boolean =  true, backStackName: String? = null)
    fun toMoviesList(@IdRes container: Int, backStackName: String? = null)
    fun toSavedFragment(@IdRes container: Int, backStackName: String? = null)
    fun toMovieDetails(@IdRes container: Int, backStackName: String? = null, fromSearchView: Boolean = false)
    fun toSearchView(@IdRes container: Int, name: String? = null)

}