package com.example.architecturesandbox.movie.data.net

import com.example.architecturesandbox.movie.data.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("movie/now_playing")
    suspend fun searchMovies(@Query("api_key") api_key: String): Response<MovieResponse?>

}