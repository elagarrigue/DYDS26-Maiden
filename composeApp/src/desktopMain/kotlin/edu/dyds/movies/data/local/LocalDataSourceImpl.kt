package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class LocalDataSourceImpl : LocalDataSource {
    private var cachedMovies: List<Movie> = emptyList()

    override val movies: List<Movie>
        get() = cachedMovies

    override suspend fun saveMovies(movies: List<Movie>) {
        cachedMovies = movies
    }
}

