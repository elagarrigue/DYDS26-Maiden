package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakePopularMoviesExternalSource(
    private val moviesToReturn: List<Movie> = emptyList(),
    private val shouldThrow: Boolean = false
) : PopularMoviesExternalSource {
    var calls = 0

    override suspend fun getPopularMovies(): List<Movie> {
        calls++
        if (shouldThrow) throw RuntimeException("Error")
        return moviesToReturn
    }
}
