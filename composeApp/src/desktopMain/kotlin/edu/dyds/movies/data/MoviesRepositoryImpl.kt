package edu.dyds.movies.data

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val popularMoviesExternalSource: PopularMoviesExternalSource,
    private val movieExternalSource: MovieExternalSource,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cachedMovies = localDataSource.movies
        if (cachedMovies.isNotEmpty()) {
            return cachedMovies
        }

        return try {
            val movies = popularMoviesExternalSource.getPopularMovies()
            localDataSource.saveMovies(movies)
            movies
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(title: String): Movie? {
        if (title.isBlank()) return null

        val cachedMovie = localDataSource.getMovieDetail(title)
        if (cachedMovie != null) {
            return cachedMovie
        }

        return try {
            val movie = movieExternalSource.getMovieByTitle(title)
            if (movie != null) {
                localDataSource.saveMovieDetail(title, movie)
            }
            movie
        } catch (e: Exception) {
            null
        }
    }
}
