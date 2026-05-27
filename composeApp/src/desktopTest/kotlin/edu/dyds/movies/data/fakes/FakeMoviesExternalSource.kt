package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesExternalSource(
    private val moviesToReturn: List<Movie> = emptyList(),
    private val shouldThrow: Boolean = false
) : MoviesExternalSource {
    var calls = 0

    override suspend fun getPopularMovies(): List<Movie> {
        calls++
        if (shouldThrow) throw RuntimeException("Error")
        return moviesToReturn
    }
}
