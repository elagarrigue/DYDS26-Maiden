package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MoviesRepositoryImpl(private val client: HttpClient) : MoviesRepository {
    override suspend fun getPopularMovies(): List<Movie> {
        return try {
            val result: RemoteResult = client.get("/3/discover/movie?sort_by=popularity.desc").body()
            result.results.map { it.toDomainMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return try {
            val movie: RemoteMovie = client.get("/3/movie/$id").body()
            movie.toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }
}



