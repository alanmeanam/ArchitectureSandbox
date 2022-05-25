package com.example.architecturesandbox.movie.domain

import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.data.repository.MoviesRepository
import javax.inject.Inject

class SaveMovieUseCase @Inject constructor(private val repository: MoviesRepository) {

    suspend operator fun invoke(movie: Movie) {
        repository.saveMovie(movie = movie)
    }

}