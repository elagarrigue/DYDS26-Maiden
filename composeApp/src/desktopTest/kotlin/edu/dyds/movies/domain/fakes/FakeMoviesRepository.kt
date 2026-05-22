package edu.dyds.movies.domain.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class FakeMoviesRepository(
    private val movieToReturn: Movie?
) : MoviesRepository {
    var calls = 0
    var lastRequestedId: Int? = null

    override suspend fun getPopularMovies(): List<Movie> = emptyList()

    override suspend fun getMovieDetails(id: Int): Movie? {
        calls++
        lastRequestedId = id
        return movieToReturn
    }
}
