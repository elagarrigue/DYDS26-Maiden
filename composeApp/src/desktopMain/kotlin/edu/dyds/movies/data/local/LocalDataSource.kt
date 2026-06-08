package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

interface LocalDataSource {
    val movies: List<Movie>
    suspend fun saveMovies(movies: List<Movie>)
    fun getMovieDetail(title: String): Movie?
    suspend fun saveMovieDetail(title: String, movie: Movie)
}

