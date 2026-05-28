package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMovieExternalSource(
    private val movieToReturn: Movie? = null,
    private val shouldThrow: Boolean = false
) : MovieExternalSource {
    var calls = 0
    var lastRequestedTitle: String? = null

    override suspend fun getMovieByTitle(title: String): Movie? {
        calls++
        lastRequestedTitle = title
        if (shouldThrow) throw RuntimeException("Error")
        return movieToReturn
    }
}
