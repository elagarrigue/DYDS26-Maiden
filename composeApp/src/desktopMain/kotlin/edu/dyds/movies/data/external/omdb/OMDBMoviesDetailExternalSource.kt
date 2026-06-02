package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

internal class OMDBMoviesExternalSource(
    private val client: HttpClient
) : MovieExternalSource {

    override suspend fun getMovieByTitle(title: String): Movie? {
        val response = try {
            client.get("/") {
                parameter("t", title)
            }
        } catch (e: Exception) {
            return null
        }
        
        if (response.status == HttpStatusCode.Unauthorized) {
            return null
        }
        
        val remoteMovie = try {
            response.body<RemoteMovie>()
        } catch (e: Exception) {
            return null
        }

        return if (remoteMovie.response == "True") {
            remoteMovie.toDomainMovie()
        } else {
            null
        }
    }
}
