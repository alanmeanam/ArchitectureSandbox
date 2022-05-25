package com.example.architecturesandbox.movie.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MovieEntity: RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Long = 0

    @SerializedName("adult")
    @Expose
    var adult: Boolean = false

    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = ""

    @SerializedName("original_language")
    @Expose
    var originalLanguage: String = ""

    @SerializedName("original_title")
    @Expose
    var originalTitle: String = ""

    @SerializedName("overview")
    @Expose
    var overview: String = ""

    @SerializedName("popularity")
    @Expose
    var popularity: Float = 0F
    @SerializedName("poster_path")
    @Expose
    var posterPath: String = ""

    @SerializedName("release_date")
    @Expose
    var releaseDate: String = ""

    @SerializedName("title")
    @Expose
    var title: String = ""

    @SerializedName("video")
    @Expose
    var video: Boolean = false

    @SerializedName("vote_average")
    @Expose
    var voteAverage: Float = 0F

    @SerializedName("vote_count")
    @Expose
    var voteCount: Float = 0F

}