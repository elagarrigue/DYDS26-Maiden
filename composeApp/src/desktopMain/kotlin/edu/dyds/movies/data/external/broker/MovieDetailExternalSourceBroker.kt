package edu.dyds.movies.data.external.broker

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

internal class MovieExternalSourceBroker(
    private val tmdbSource: MovieExternalSource,
    private val omdbSource: MovieExternalSource
) : MovieExternalSource {

    override suspend fun getMovieByTitle(title: String): Movie? = supervisorScope {
        val tmdbDeferred = async {
            try {
                tmdbSource.getMovieByTitle(title)
            } catch (e: Exception) {
                null
            }
        }
        val omdbDeferred = async {
            try {
                omdbSource.getMovieByTitle(title)
            } catch (e: Exception) {
                null
            }
        }

        val tmdbMovie = tmdbDeferred.await()
        val omdbMovie = omdbDeferred.await()

        combine(tmdbMovie, omdbMovie)
    }

    private fun combine(tmdb: Movie?, omdb: Movie?): Movie? {
        return when {
            tmdb != null && omdb != null -> {
                tmdb.copy(
                    overview = "TMDB: ${tmdb.overview}\n\nOMDB: ${omdb.overview}",
                    popularity = (tmdb.popularity + omdb.popularity) / 2.0,
                    voteAverage = (tmdb.voteAverage + omdb.voteAverage) / 2.0
                )
            }
            tmdb != null -> {
                tmdb.copy(overview = "TMDB: ${tmdb.overview}")
            }
            omdb != null -> {
                omdb.copy(overview = "OMDB: ${omdb.overview}")
            }
            else -> null
        }
    }
}
