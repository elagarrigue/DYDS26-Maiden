package edu.dyds.movies.presentation.fakes

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.yield

class FakeGetMoviesUseCase(
    private val result: List<QualifiedMovie> = emptyList(),
    private val exceptionToThrow: Exception? = null,
) : GetPopularMoviesUseCase {
    var calls = 0

    override suspend fun invoke(): List<QualifiedMovie> {
        calls++
        yield()
        exceptionToThrow?.let { throw it }
        return result
    }
}
