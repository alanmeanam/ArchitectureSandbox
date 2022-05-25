package com.example.architecturesandbox.common.di.app

import com.example.architecturesandbox.BuildConfig
import com.example.architecturesandbox.base.BaseServiceObject
import com.example.architecturesandbox.common.di.qualifiers.GsonBase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @AppScope
    @GsonBase
    fun provideGson(): Gson = Gson()

    @Provides
    @AppScope
    fun provideGsonBuilder(): Gson =
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Provides
    @AppScope
    fun provideMovieRetrofit(serviceObject: BaseServiceObject): Retrofit.Builder =
        serviceObject(baseUrl = BuildConfig.MOVIE_BASE_URL)

}