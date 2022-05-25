package com.example.architecturesandbox.movie.domain

import com.example.architecturesandbox.common.di.app.DefaultDispatcher
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.utils.StringUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ArrangeInfoUseCase @Inject constructor(
    private val stringUtil: StringUtil,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
    ) {

    suspend operator fun invoke(movie: Movie): Flow<List<Pair<String, String>>> = flow {
        val detailsList = mutableListOf<Pair<String, String>>()
        val movieJson = stringUtil.modelToJson(model = movie)
        movieJson?.let { json ->
            val id = "${json["id"]}"
            detailsList.add(Pair(first = "id", second = id))

            val adult = "${json["adult"]}"
            detailsList.add(Pair(first = "adult", second = adult))

            val video = "${json["video"]}"
            detailsList.add(Pair(first = "video", second = video))

            val popularity = "${json["popularity"]}"
            detailsList.add(Pair(first = "popularity", second = popularity))

            val voteAverage = "${json["voteAverage"]}"
            detailsList.add(Pair(first = "voteAverage", second = voteAverage))

            val voteCount = "${json["voteCount"]}"
            detailsList.add(Pair(first = "voteCount", second = voteCount))

            val originalTitle = "${json["originalTitle"]}"
            detailsList.add(Pair(first = "originalTitle", second = originalTitle))

            val releaseDate = "${json["releaseDate"]}"
            detailsList.add(Pair(first = "releaseDate", second = releaseDate))
        }
        emit(detailsList)
    }.flowOn(defaultDispatcher)

}