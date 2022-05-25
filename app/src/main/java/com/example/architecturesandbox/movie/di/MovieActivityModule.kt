package com.example.architecturesandbox.movie.di

import com.example.architecturesandbox.movie.presentation.navigator.MovieScreenNavigator
import com.example.architecturesandbox.movie.presentation.navigator.MovieScreenNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class MovieActivityModule {

    @ActivityScoped
    @Binds
    abstract fun screenNavigator(navigator: MovieScreenNavigatorImpl): MovieScreenNavigator

}