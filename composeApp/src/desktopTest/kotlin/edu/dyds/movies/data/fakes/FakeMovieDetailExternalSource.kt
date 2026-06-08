package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMovieDetailExternalSource(
    private val movieToReturn: Movie? = null,
    private val shouldThrow: Boolean = false
) : MovieDetailExternalSource {
    var calls = 0
    var lastRequestedTitle: String? = null

    override suspend fun getMovieByTitle(title: String): Movie? {
        calls++
        lastRequestedTitle = title
        if (shouldThrow) throw RuntimeException("Error")
        return movieToReturn
    }
}
