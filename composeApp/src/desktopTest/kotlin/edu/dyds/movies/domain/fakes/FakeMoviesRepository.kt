package edu.dyds.movies.domain.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class FakeMoviesRepository(
    private val movieToReturn: Movie?
) : MoviesRepository {
    var calls = 0
    var lastRequestedTitle: String? = null

    override suspend fun getPopularMovies(): List<Movie> = emptyList()

    override suspend fun getMovieDetails(title: String): Movie? {
        calls++
        lastRequestedTitle = title
        return movieToReturn
    }
}
