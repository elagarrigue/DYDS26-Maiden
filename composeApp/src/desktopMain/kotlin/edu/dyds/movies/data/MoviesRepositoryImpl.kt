package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(private val remoteDataSource: RemoteDataSource) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        return try {
            val result = remoteDataSource.getPopularMovies()   
            result.results.map { it.toDomainMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return try {
            val movie = remoteDataSource.getMovieDetails(id)
            movie.toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }
}



