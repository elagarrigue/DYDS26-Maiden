package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.fakes.FakeMoviesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class GetMovieDetailsUseCaseImplTest {

    @Test
    fun `delega en repositorio y retorna pelicula`() = runTest {
        val expectedMovie = buildMovie(id = 9)
        val repository = FakeMoviesRepository(movieToReturn = expectedMovie)
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        val result = useCase("Pelicula 9")

        assertEquals(expectedMovie, result)
        assertEquals(1, repository.calls)
        assertEquals("Pelicula 9", repository.lastRequestedTitle)
    }

    @Test
    fun `retorna null cuando repositorio retorna null`() = runTest {
        val repository = FakeMoviesRepository(movieToReturn = null)
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        val result = useCase("Pelicula 4")

        assertNull(result)
        assertEquals(1, repository.calls)
        assertEquals("Pelicula 4", repository.lastRequestedTitle)
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

}
