package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteDataSource
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import edu.dyds.movies.data.external.RemoteMoviesExternalSourceAdapter
import edu.dyds.movies.data.local.LocalDataSource
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
            ),
            Movie(
                id = 2,
                title = "Cached Movie 2",
                overview = "Cached Overview 2",
                releaseDate = "2023-02-01",
                poster = "cached_poster2",
                backdrop = null,
                originalTitle = "Cached Original 2",
                originalLanguage = "es",
                popularity = 8.0,
                voteAverage = 6.0
            )
        )
        var remoteCalled = false
        val fakeRemoteDataSource = object : RemoteDataSource {
            override suspend fun getPopularMovies(): RemoteResult {
                remoteCalled = true
                return RemoteResult(1, emptyList(), 1, 0)
            }
            override suspend fun getMovieDetails(id: Int): RemoteMovie = throw NotImplementedError()
        }
        val fakeLocalDataSource = object : LocalDataSource {
            override val movies: List<Movie> get() = cachedMovies
            override suspend fun saveMovies(movies: List<Movie>) {}
        }
        val externalSource = RemoteMoviesExternalSourceAdapter(fakeRemoteDataSource)
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = externalSource,
            movieExternalSource = externalSource,
            localDataSource = fakeLocalDataSource
        )

        // When
        val result = repository.getPopularMovies()

        // Then
        assertEquals(cachedMovies, result)
        assertFalse(remoteCalled)
    }
}
