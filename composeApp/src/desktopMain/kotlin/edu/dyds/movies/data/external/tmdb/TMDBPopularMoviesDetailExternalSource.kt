package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.normalizeTitle
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class TMDBPopularMoviesDetailExternalSource(
    private val client: HttpClient
) : PopularMoviesExternalSource, MovieDetailExternalSource {

    override suspend fun getPopularMovies(): List<Movie> {
        return client.get("/3/discover/movie?sort_by=popularity.desc")
            .body<RemoteResult>()
            .results
            .map { it.toDomainMovie() }
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        val remoteResult = client.get("/3/search/movie") {
            parameter("query", title)
        }.body<RemoteResult>()

        if (remoteResult.results.isEmpty()) return null

        val normalizedSearchTitle = normalizeTitle(title)
        val exactMatch = remoteResult.results.find { 
            normalizeTitle(it.title) == normalizedSearchTitle 
        }

        return (exactMatch ?: remoteResult.results.first()).toDomainMovie()
    }
}
