package edu.dyds.movies.data

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val moviesExternalSource: MoviesExternalSource,
    private val movieExternalSource: MovieExternalSource,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cachedMovies = localDataSource.movies
        if (cachedMovies.isNotEmpty()) {
            return cachedMovies
        }

        return try {
            val movies = moviesExternalSource.getPopularMovies()
            localDataSource.saveMovies(movies)
            movies
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(title: String): Movie? {
        if (title.isBlank()) return null

        val normalizedTitle = normalizeTitle(title)
        val cachedMovie = localDataSource.getMovieDetail(normalizedTitle)
        if (cachedMovie != null) {
            return cachedMovie
        }

        return try {
            val movie = movieExternalSource.getMovieByTitle(title)
            if (movie != null) {
                localDataSource.saveMovieDetail(normalizedTitle, movie)
            }
            movie
        } catch (e: Exception) {
            null
        }
    }
}
