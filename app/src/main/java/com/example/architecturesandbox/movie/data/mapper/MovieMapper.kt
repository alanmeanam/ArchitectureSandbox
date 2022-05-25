package com.example.architecturesandbox.movie.data.mapper

import com.example.architecturesandbox.common.mapper.EntityMapper
import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.data.model.MovieEntity
import javax.inject.Inject

class MovieMapper @Inject constructor(): EntityMapper<MovieEntity, Movie> {

    override fun mapToEntity(domainModel: Movie): MovieEntity =
        MovieEntity().apply {
            id = domainModel.id.toLong()
            adult = domainModel.adult
            backdropPath = domainModel.backdropPath
            originalLanguage = domainModel.originalLanguage
            originalTitle = domainModel.originalTitle
            overview = domainModel.overview
            popularity = domainModel.popularity
            posterPath = domainModel.posterPath
            releaseDate = domainModel.releaseDate
            title = domainModel.title
            video = domainModel.video
            voteAverage = domainModel.voteAverage
            voteCount = domainModel.voteCount
        }

    override fun mapFromEntity(entity: MovieEntity): Movie =
        Movie().apply {
            id = entity.id.toInt()
            adult = entity.adult
            backdropPath = entity.backdropPath
            originalLanguage = entity.originalLanguage
            originalTitle = entity.originalTitle
            overview = entity.overview
            popularity = entity.popularity
            posterPath = entity.posterPath
            releaseDate = entity.releaseDate
            title = entity.title
            video = entity.video
            voteAverage = entity.voteAverage
            voteCount = entity.voteCount
        }

    fun mapFromEntityList(entities: List<MovieEntity>): List<Movie> = entities.map { mapFromEntity(it) }

}