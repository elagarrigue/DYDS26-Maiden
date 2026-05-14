package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteDataSource
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MoviesRepositoryImplPopularRemoteErrorTest {

    @Test
    fun `deberia retornar lista vacia cuando remoto falla`() = runTest {
        // Given
        val fakeRemoteDataSource = object : RemoteDataSource {
            override suspend fun getPopularMovies(): RemoteResult = throw Exception("Network error")
            override suspend fun getMovieDetails(id: Int): RemoteMovie = throw NotImplementedError()
        }
        val fakeLocalDataSource = object : LocalDataSource {
            override val movies: List<Movie> get() = emptyList()
            override suspend fun saveMovies(movies: List<Movie>) {}
        }
        val repository = MoviesRepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource)

        // When
        val result = repository.getPopularMovies()

        // Then
        assertTrue(result.isEmpty())
    }
}
