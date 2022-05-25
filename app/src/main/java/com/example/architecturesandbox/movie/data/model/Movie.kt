package com.example.architecturesandbox.movie.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(

    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("adult")
    var adult: Boolean = false,

    @SerializedName("backdropPath")
    var backdropPath: String? = "",

    @SerializedName("genreIds")
    var genreIds: List<Int>? = null,

    @SerializedName("originalLanguage")
    var originalLanguage: String = "",

    @SerializedName("originalTitle")
    var originalTitle: String = "",

    @SerializedName("overview")
    var overview: String = "",

    @SerializedName("popularity")
    var popularity: Float = 0F,

    @SerializedName("posterPath")
    var posterPath: String = "",

    @SerializedName("releaseDate")
    var releaseDate: String = "",

    @field:SerializedName("title")
    var title: String = "",

    @SerializedName("video")
    var video: Boolean = false,

    @SerializedName("voteAverage")
    var voteAverage: Float = 0F,

    @SerializedName("voteCount")
    var voteCount: Float = 0F,

    @SerializedName("isHeader")
    var isHeader: Boolean = false

): Parcelable