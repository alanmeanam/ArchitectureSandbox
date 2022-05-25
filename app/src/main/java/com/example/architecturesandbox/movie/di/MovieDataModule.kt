package com.example.architecturesandbox.movie.di

import com.example.architecturesandbox.movie.data.model.MovieEntity
import com.example.architecturesandbox.movie.data.net.MovieService
import com.example.architecturesandbox.movie.data.repository.MoviesRepository
import com.example.architecturesandbox.movie.data.repository.MoviesRepositoryImpl
import com.example.architecturesandbox.realm.RealmBaseContract
import com.example.architecturesandbox.realm.RealmBaseInteractor
import com.example.architecturesandbox.utils.StringUtil
import com.example.architecturesandbox.utils.StringUtilImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class MovieDataModule {

    @Binds
    abstract fun bindRepository(moviesRepositoryImpl: MoviesRepositoryImpl): MoviesRepository

    @Binds
    abstract fun bindStringUtil(stringUtilImpl: StringUtilImpl): StringUtil

    companion object {

        @Provides
        fun provideMovieService(retrofit: Retrofit.Builder): MovieService =
            retrofit
                .build()
                .create(MovieService::class.java)

        @Provides
        fun provideMovieInteractor(): RealmBaseContract<MovieEntity> =
            RealmBaseInteractor(objClass = MovieEntity::class.java)

    }

}