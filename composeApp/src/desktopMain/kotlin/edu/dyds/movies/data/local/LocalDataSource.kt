package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

interface LocalDataSource {
    val movies: List<Movie>
    suspend fun saveMovies(movies: List<Movie>)
    fun getMovieDetail(normalizedTitle: String): Movie?
    suspend fun saveMovieDetail(normalizedTitle: String, movie: Movie)
}

