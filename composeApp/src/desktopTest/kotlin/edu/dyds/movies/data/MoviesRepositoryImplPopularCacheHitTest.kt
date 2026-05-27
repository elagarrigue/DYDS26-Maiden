package edu.dyds.movies.data

import edu.dyds.movies.data.fakes.FakeLocalDataSource
import edu.dyds.movies.data.fakes.FakeMovieExternalSource
import edu.dyds.movies.data.fakes.FakeMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.coroutines.test.runTest

class MoviesRepositoryImplPopularCacheHitTest {

    @Test
    fun `deberia retornar cache sin llamar remoto cuando hay cache`() = runTest {
        // Given
        val cachedMovies = listOf(
            Movie(
                id = 1,
                title = "Cached Movie 1",
                overview = "Cached Overview 1",
                releaseDate = "2023-01-01",
                poster = "cached_poster1",
                backdrop = "cached_backdrop1",
                originalTitle = "Cached Original 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 7.5
            )
        )
        
        val moviesSource = FakeMoviesExternalSource()
        val movieSource = FakeMovieExternalSource()
        val local = FakeLocalDataSource(cachedMovies)
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = moviesSource,
            movieExternalSource = movieSource,
            localDataSource = local
        )

        // When
        val result = repository.getPopularMovies()

        // Then
        assertEquals(cachedMovies, result)
        assertEquals(0, moviesSource.calls)
    }
}
