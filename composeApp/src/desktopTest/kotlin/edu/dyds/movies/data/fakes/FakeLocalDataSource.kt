package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie

class FakeLocalDataSource(initialMovies: List<Movie>) : LocalDataSource {
    private var cachedMovies: List<Movie> = initialMovies
    private val cachedDetails = mutableMapOf<String, Movie>()

    override val movies: List<Movie>
        get() = cachedMovies

    override suspend fun saveMovies(movies: List<Movie>) {
        cachedMovies = movies
    }

    override fun getMovieDetail(normalizedTitle: String): Movie? {
        return cachedDetails[normalizedTitle]
    }

    override suspend fun saveMovieDetail(normalizedTitle: String, movie: Movie) {
        cachedDetails[normalizedTitle] = movie
    }
}
