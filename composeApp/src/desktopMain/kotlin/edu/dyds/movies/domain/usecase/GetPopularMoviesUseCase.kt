package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

class GetPopularMoviesUseCase(
    private val repository: MoviesRepository
) {
    private val minVoteAverage = 6.0

    suspend operator fun invoke(): List<QualifiedMovie> {
        return repository.getPopularMovies()
            .sortedByDescending { it.voteAverage }
            .map {
                QualifiedMovie(
                    movie = it,
                    isGoodMovie = it.voteAverage >= minVoteAverage
                )
            }
    }
}
