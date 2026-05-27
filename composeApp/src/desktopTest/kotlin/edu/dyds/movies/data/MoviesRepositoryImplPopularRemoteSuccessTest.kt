package edu.dyds.movies.data

import edu.dyds.movies.data.fakes.FakeLocalDataSource
import edu.dyds.movies.data.fakes.FakeMovieExternalSource
import edu.dyds.movies.data.fakes.FakeMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class MoviesRepositoryImplPopularRemoteSuccessTest {

    @Test
    fun `deberia mapear RemoteMovie a Movie y guardar cache cuando remoto exitoso`() = runTest {
        // Given
        val domainMovies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                poster = "https://image.tmdb.org/t/p/w185/poster1.jpg",
                backdrop = "https://image.tmdb.org/t/p/w780/backdrop1.jpg",
                originalTitle = "Original 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 7.5
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                overview = "Overview 2",
                releaseDate = "2023-02-01",
                poster = "https://image.tmdb.org/t/p/w185/poster2.jpg",
                backdrop = null,
                originalTitle = "Original 2",
                originalLanguage = "es",
                popularity = 8.0,
                voteAverage = 6.0
            )
        )
        
        val moviesSource = FakeMoviesExternalSource(moviesToReturn = domainMovies)
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
        assertEquals(2, result.size)
        assertEquals(domainMovies, result)
        assertEquals(domainMovies, local.movies)
    }
}
