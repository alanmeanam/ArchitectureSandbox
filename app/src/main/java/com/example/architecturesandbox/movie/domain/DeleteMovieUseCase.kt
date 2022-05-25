package com.example.architecturesandbox.movie.domain

import com.example.architecturesandbox.movie.data.repository.MoviesRepository
import javax.inject.Inject

class DeleteMovieUseCase @Inject constructor(private val repository: MoviesRepository) {

    suspend operator fun invoke(id: Long) {
        repository.deleteMovie(id = id)
    }

}