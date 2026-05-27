package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteMoviesExternalSourceAdapter
import edu.dyds.movies.data.fakes.FakeLocalDataSource
import edu.dyds.movies.data.fakes.FakeRemoteDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class MoviesRepositoryImplDetailTest {

    @Test
    fun `cuando hay cache retorna pelicula y no llama remoto`() = runTest {
        val cachedMovie = buildMovie(id = 7)
        val local = FakeLocalDataSource(listOf(cachedMovie))
        val remote = FakeRemoteDataSource()
        val externalSource = RemoteMoviesExternalSourceAdapter(remote)
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = externalSource,
            movieExternalSource = externalSource,
            localDataSource = local
        )

        val result = repository.getMovieDetails(cachedMovie.id)

        assertEquals(cachedMovie, result)
        assertEquals(0, remote.getMovieDetailsCalls)
    }

    @Test
    fun `cuando no hay cache llama remoto y mapea pelicula`() = runTest {
        val remoteMovie = buildRemoteMovie(
            id = 12,
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg"
        )
        val local = FakeLocalDataSource(emptyList())
        val remote = FakeRemoteDataSource(movieToReturn = remoteMovie)
        val externalSource = RemoteMoviesExternalSourceAdapter(remote)
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = externalSource,
            movieExternalSource = externalSource,
            localDataSource = local
        )

        val result = repository.getMovieDetails(12)

        val expected = Movie(
            id = remoteMovie.id,
            title = remoteMovie.title,
            overview = remoteMovie.overview,
            releaseDate = remoteMovie.releaseDate,
            poster = "https://image.tmdb.org/t/p/w185${remoteMovie.posterPath}",
            backdrop = remoteMovie.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
            originalTitle = remoteMovie.originalTitle,
            originalLanguage = remoteMovie.originalLanguage,
            popularity = remoteMovie.popularity,
            voteAverage = remoteMovie.voteAverage
        )

        assertEquals(1, remote.getMovieDetailsCalls)
        assertEquals(12, remote.lastRequestedId)
        assertEquals(expected, result)
    }

    @Test
    fun `cuando remoto falla retorna null`() = runTest {
        val local = FakeLocalDataSource(emptyList())
        val remote = FakeRemoteDataSource(shouldThrow = true)
        val externalSource = RemoteMoviesExternalSourceAdapter(remote)
        val repository = MoviesRepositoryImpl(
            moviesExternalSource = externalSource,
            movieExternalSource = externalSource,
            localDataSource = local
        )

        val result = repository.getMovieDetails(5)

        assertEquals(1, remote.getMovieDetailsCalls)
        assertEquals(5, remote.lastRequestedId)
        assertNull(result)
    }

    private fun buildMovie(id: Int): Movie {
        return Movie(
            id = id,
            title = "Pelicula $id",
            overview = "Overview $id",
            releaseDate = "2024-01-01",
            poster = "https://image.tmdb.org/t/p/w185/poster.jpg",
            backdrop = "https://image.tmdb.org/t/p/w780/backdrop.jpg",
            originalTitle = "Original $id",
            originalLanguage = "en",
            popularity = 7.5,
            voteAverage = 6.7
        )
    }

    private fun buildRemoteMovie(
        id: Int,
        posterPath: String = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg"
    ): RemoteMovie {
        return RemoteMovie(
            id = id,
            title = "Pelicula $id",
            overview = "Overview $id",
            releaseDate = "2024-01-01",
            posterPath = posterPath,
            backdropPath = backdropPath,
            originalTitle = "Original $id",
            originalLanguage = "en",
            popularity = 7.5,
            voteAverage = 6.7
        )
    }

}
