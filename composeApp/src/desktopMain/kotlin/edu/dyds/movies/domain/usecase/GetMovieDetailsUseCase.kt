package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

interface GetMovieDetailsUseCase {
    suspend operator fun invoke(id: Int): Movie?
}

class GetMovieDetailsUseCaseImpl(
    private val repository: MoviesRepository
) : GetMovieDetailsUseCase {
    override suspend operator fun invoke(id: Int): Movie? {
        return repository.getMovieDetails(id)
    }
}
