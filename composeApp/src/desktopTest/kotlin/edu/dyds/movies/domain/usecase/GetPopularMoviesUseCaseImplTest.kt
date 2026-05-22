package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetPopularMoviesUseCaseImplTest {

    @Test
    fun `deberia ordenar peliculas por voteAverage descendente`() = runTest {
        // Given
        val movies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                poster = "poster1",
                backdrop = "backdrop1",
                originalTitle = "Original 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 5.0
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                overview = "Overview 2",
                releaseDate = "2023-02-01",
                poster = "poster2",
                backdrop = "backdrop2",
                originalTitle = "Original 2",
                originalLanguage = "es",
                popularity = 8.0,
                voteAverage = 7.5
            ),
            Movie(
                id = 3,
                title = "Movie 3",
                overview = "Overview 3",
                releaseDate = "2023-03-01",
                poster = "poster3",
                backdrop = null,
                originalTitle = "Original 3",
                originalLanguage = "fr",
                popularity = 9.0,
                voteAverage = 6.0
            )
        )
        val fakeRepository = object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = movies
            override suspend fun getMovieDetails(id: Int): Movie? = null
        }
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // When
        val result = useCase()

        // Then
        assertEquals(3, result.size)
        assertEquals(7.5, result[0].movie.voteAverage)
        assertEquals(6.0, result[1].movie.voteAverage)
        assertEquals(5.0, result[2].movie.voteAverage)
    }

    @Test
    fun `deberia asignar isGoodMovie correctamente con umbral 6_0`() = runTest {
        // Given
        val movies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                poster = "poster1",
                backdrop = "backdrop1",
                originalTitle = "Original 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 6.0
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                overview = "Overview 2",
                releaseDate = "2023-02-01",
                poster = "poster2",
                backdrop = "backdrop2",
                originalTitle = "Original 2",
                originalLanguage = "es",
                popularity = 8.0,
                voteAverage = 5.9
            ),
            Movie(
                id = 3,
                title = "Movie 3",
                overview = "Overview 3",
                releaseDate = "2023-03-01",
                poster = "poster3",
                backdrop = null,
                originalTitle = "Original 3",
                originalLanguage = "fr",
                popularity = 9.0,
                voteAverage = 7.0
            )
        )
        val fakeRepository = object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = movies
            override suspend fun getMovieDetails(id: Int): Movie? = null
        }
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // When
        val result = useCase()

        // Then
        assertTrue(result.any { it.isGoodMovie && it.movie.voteAverage == 6.0 })
        assertTrue(result.any { !it.isGoodMovie && it.movie.voteAverage == 5.9 })
        assertTrue(result.any { it.isGoodMovie && it.movie.voteAverage == 7.0 })
    }

    @Test
    fun `deberia preservar todos los elementos con mismo tamano e ids`() = runTest {
        // Given
        val movies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                poster = "poster1",
                backdrop = "backdrop1",
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
                poster = "poster2",
                backdrop = null,
                originalTitle = "Original 2",
                originalLanguage = "es",
                popularity = 8.0,
                voteAverage = 6.0
            )
        )
        val fakeRepository = object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = movies
            override suspend fun getMovieDetails(id: Int): Movie? = null
        }
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // When
        val result = useCase()

        // Then
        assertEquals(2, result.size)
        assertEquals(setOf(1, 2), result.map { it.movie.id }.toSet())
    }

    @Test
    fun `deberia retornar lista vacia cuando repositorio retorna vacio`() = runTest {
        // Given
        val fakeRepository = object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = emptyList()
            override suspend fun getMovieDetails(id: Int): Movie? = null
        }
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isEmpty())
    }
}
