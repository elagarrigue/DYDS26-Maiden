package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.RemoteDataSource
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult

class FakeRemoteDataSource(
    private val movieToReturn: RemoteMovie? = null,
    private val shouldThrow: Boolean = false
) : RemoteDataSource {
    var getMovieDetailsCalls = 0
    var lastRequestedId: Int? = null

    override suspend fun getPopularMovies(): RemoteResult {
        return RemoteResult(page = 1, results = emptyList(), totalPages = 1, totalResults = 0)
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie {
        getMovieDetailsCalls++
        lastRequestedId = id
        if (shouldThrow) {
            throw RuntimeException("Falla remota")
        }
        return movieToReturn ?: error("RemoteMovie faltante")
    }
}
