package com.example.architecturesandbox.movie.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieResponse (

    @SerializedName("dates")
    @Expose
    var dates: MovieDates? = null,

    @SerializedName("page")
    @Expose
    var page: Int = 0,

    @SerializedName("results")
    @Expose
    var results: List<MovieEntity>? = null

)