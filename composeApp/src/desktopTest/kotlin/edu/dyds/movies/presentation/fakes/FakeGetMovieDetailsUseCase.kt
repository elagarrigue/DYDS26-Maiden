package edu.dyds.movies.presentation.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.yield

class FakeGetMovieDetailsUseCase(
    private val result: Movie?
) : GetMovieDetailsUseCase {
    var calls = 0
    var lastRequestedId: Int? = null

    override suspend fun invoke(id: Int): Movie? {
        calls++
        lastRequestedId = id
        yield()
        return result
    }
}
