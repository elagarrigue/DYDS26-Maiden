package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class LocalDataSourceImpl : LocalDataSource {
    private var cachedMovies: List<Movie> = emptyList()
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

