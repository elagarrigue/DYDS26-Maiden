package edu.dyds.movies.data.external.broker

import edu.dyds.movies.data.fakes.FakeMovieExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class MovieExternalSourceBrokerTest {

    @Test
    fun `cuando ambos responden combina resultados correctamente`() = runTest {
        val tmdbMovie = buildMovie(title = "Inception", overview = "TMDB Plot", popularity = 10.0, voteAverage = 8.0)
        val omdbMovie = buildMovie(title = "Inception", overview = "OMDB Plot", popularity = 8.0, voteAverage = 7.0)
        
        val tmdbSource = FakeMovieExternalSource(movieToReturn = tmdbMovie)
        val omdbSource = FakeMovieExternalSource(movieToReturn = omdbMovie)
        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        val expected = tmdbMovie.copy(
            overview = "TMDB: TMDB Plot\n\nOMDB: OMDB Plot",
            popularity = 9.0, // (10+8)/2
            voteAverage = 7.5  // (8+7)/2
        )
        assertEquals(expected, result)
    }

    @Test
    fun `cuando solo TMDB responde retorna TMDB con prefijo`() = runTest {
        val tmdbMovie = buildMovie(title = "Inception", overview = "TMDB Plot")
        val tmdbSource = FakeMovieExternalSource(movieToReturn = tmdbMovie)
        val omdbSource = FakeMovieExternalSource(movieToReturn = null)
        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        val expected = tmdbMovie.copy(overview = "TMDB: TMDB Plot")
        assertEquals(expected, result)
    }

    @Test
    fun `cuando solo OMDB responde retorna OMDB con prefijo`() = runTest {
        val omdbMovie = buildMovie(title = "Inception", overview = "OMDB Plot")
        val tmdbSource = FakeMovieExternalSource(movieToReturn = null)
        val omdbSource = FakeMovieExternalSource(movieToReturn = omdbMovie)
        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        val expected = omdbMovie.copy(overview = "OMDB: OMDB Plot")
        assertEquals(expected, result)
    }

    @Test
    fun `cuando ninguno responde retorna null`() = runTest {
        val tmdbSource = FakeMovieExternalSource(movieToReturn = null)
        val omdbSource = FakeMovieExternalSource(movieToReturn = null)
        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        assertNull(result)
    }

    @Test
    fun `cuando un servicio falla sigue funcionando con el otro`() = runTest {
        val tmdbMovie = buildMovie(title = "Inception", overview = "TMDB Plot")
        val tmdbSource = FakeMovieExternalSource(movieToReturn = tmdbMovie)
        val omdbSource = FakeMovieExternalSource(shouldThrow = true)
        val broker = MovieExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieByTitle("Inception")

        val expected = tmdbMovie.copy(overview = "TMDB: TMDB Plot")
        assertEquals(expected, result)
    }

    private fun buildMovie(
        title: String, 
        overview: String, 
        popularity: Double = 0.0, 
        voteAverage: Double = 0.0
    ): Movie {
        return Movie(
            id = title.hashCode(),
            title = title,
            overview = overview,
            releaseDate = "2024-01-01",
            poster = "poster",
            backdrop = "backdrop",
            originalTitle = title,
            originalLanguage = "en",
            popularity = popularity,
            voteAverage = voteAverage
        )
    }
}
