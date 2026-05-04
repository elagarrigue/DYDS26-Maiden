package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteDataSource
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cachedMovies = localDataSource.movies
        if (cachedMovies.isNotEmpty()) {
            return cachedMovies
        }

        return try {
            val result = remoteDataSource.getPopularMovies()   
            val movies = result.results.map { it.toDomainMovie() }
            localDataSource.saveMovies(movies)
            movies
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        val cachedMovie = localDataSource.movies.find { it.id == id }
        if (cachedMovie != null) {
            return cachedMovie
        }

        return try {
            val movie = remoteDataSource.getMovieDetails(id)
            movie.toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }
}
