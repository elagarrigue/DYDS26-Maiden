package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteDataSource
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import edu.dyds.movies.data.external.RemoteMoviesExternalSourceAdapter
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MoviesRepositoryImplPopularRemoteSuccessTest {

    @Test
    fun `deberia mapear RemoteMovie a Movie y guardar cache cuando remoto exitoso`() = runTest {
        // Given
        val remoteMovies = listOf(
            RemoteMovie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                posterPath = "/poster1.jpg",
                backdropPath = "/backdrop1.jpg",
                originalTitle = "Original 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 7.5
            ),
            RemoteMovie(
                id = 2,
                title = "Movie 2",
                overview = "Overview 2",
                releaseDate = "2023-02-01",
                posterPath = "/poster2.jpg",
                backdropPath = null,
                originalTitle = "Original 2",
                originalLanguage = "es",
                popularity = 8.0,
                voteAverage = 6.0
            )
        )
        val remoteResult = RemoteResult(1, remoteMovies, 1, 2)
        val fakeRemoteDataSource = object : RemoteDataSource {
            override suspend fun getPopularMovies(): RemoteResult = remoteResult
            override suspend fun getMovieDetails(id: Int): RemoteMovie = throw NotImplementedError()
        }
        val savedMovies = mutableListOf<Movie>()
        val fakeLocalDataSource = object : LocalDataSource {
            override val movies: List<Movie> get() = emptyList()
            override suspend fun saveMovies(movies: List<Movie>) {
                savedMovies.addAll(movies)
            }
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
        assertEquals(2, result.size)
        val movie1 = result[0]
        assertEquals(1, movie1.id)
        assertEquals("Movie 1", movie1.title)
        assertEquals("https://image.tmdb.org/t/p/w185/poster1.jpg", movie1.poster)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop1.jpg", movie1.backdrop)
        val movie2 = result[1]
        assertEquals(2, movie2.id)
        assertEquals("Movie 2", movie2.title)
        assertEquals("https://image.tmdb.org/t/p/w185/poster2.jpg", movie2.poster)
        assertNull(movie2.backdrop)
        assertEquals(2, savedMovies.size)
        assertEquals(result, savedMovies)
    }
}
