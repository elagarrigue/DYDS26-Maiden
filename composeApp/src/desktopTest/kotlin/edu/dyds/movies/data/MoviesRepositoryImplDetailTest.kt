package edu.dyds.movies.data

import edu.dyds.movies.data.fakes.FakeLocalDataSource
import edu.dyds.movies.data.fakes.FakeMovieExternalSource
import edu.dyds.movies.data.fakes.FakeMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class MoviesRepositoryImplDetailTest {

    @Test
    fun `cuando hay cache retorna pelicula y no llama remoto`() = runTest {
        val cachedMovie = buildMovie(title = "Inception")
        val local = FakeLocalDataSource(emptyList())
        local.saveMovieDetail("inception", cachedMovie)
        
        val moviesSource = FakeMoviesExternalSource()
        val movieSource = FakeMovieExternalSource()
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = moviesSource,
            movieExternalSource = movieSource,
            localDataSource = local
        )

        val result = repository.getMovieDetails("Inception")

        assertEquals(cachedMovie, result)
        assertEquals(0, movieSource.calls)
    }

    @Test
    fun `cuando no hay cache llama remoto y guarda en cache`() = runTest {
        val remoteMovie = buildMovie(title = "Avatar")
        val local = FakeLocalDataSource(emptyList())
        val moviesSource = FakeMoviesExternalSource()
        val movieSource = FakeMovieExternalSource(movieToReturn = remoteMovie)
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = moviesSource,
            movieExternalSource = movieSource,
            localDataSource = local
        )

        val result = repository.getMovieDetails("Avatar")

        assertEquals(1, movieSource.calls)
        assertEquals("Avatar", movieSource.lastRequestedTitle)
        assertEquals(remoteMovie, result)
        assertEquals(remoteMovie, local.getMovieDetail("avatar"))
    }

    @Test
    fun `cuando remoto falla retorna null`() = runTest {
        val local = FakeLocalDataSource(emptyList())
        val moviesSource = FakeMoviesExternalSource()
        val movieSource = FakeMovieExternalSource(shouldThrow = true)
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = moviesSource,
            movieExternalSource = movieSource,
            localDataSource = local
        )

        val result = repository.getMovieDetails("Unknown")

        assertEquals(1, movieSource.calls)
        assertNull(result)
    }

    private fun buildMovie(title: String): Movie {
        return Movie(
            id = title.hashCode(),
            title = title,
            overview = "Overview $title",
            releaseDate = "2024-01-01",
            poster = "https://image.tmdb.org/t/p/w185/poster.jpg",
            backdrop = "https://image.tmdb.org/t/p/w780/backdrop.jpg",
            originalTitle = "Original $title",
            originalLanguage = "en",
            popularity = 7.5,
            voteAverage = 6.7
        )
    }
}
