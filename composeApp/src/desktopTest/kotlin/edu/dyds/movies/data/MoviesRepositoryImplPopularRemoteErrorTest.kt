package edu.dyds.movies.data

import edu.dyds.movies.data.fakes.FakeLocalDataSource
import edu.dyds.movies.data.fakes.FakeMovieExternalSource
import edu.dyds.movies.data.fakes.FakeMoviesExternalSource
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MoviesRepositoryImplPopularRemoteErrorTest {

    @Test
    fun `deberia retornar lista vacia cuando remoto falla`() = runTest {
        // Given
        val moviesSource = FakeMoviesExternalSource(shouldThrow = true)
        val movieSource = FakeMovieExternalSource()
        val local = FakeLocalDataSource(emptyList())
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = moviesSource,
            movieExternalSource = movieSource,
            localDataSource = local
        )

        // When
        val result = repository.getPopularMovies()

        // Then
        assertTrue(result.isEmpty())
    }
}
