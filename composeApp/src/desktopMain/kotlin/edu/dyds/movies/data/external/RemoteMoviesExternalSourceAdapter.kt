package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

internal class RemoteMoviesExternalSourceAdapter(
    private val remoteDataSource: RemoteDataSource,
) : MoviesExternalSource, MovieExternalSource {

    override suspend fun getPopularMovies(): List<Movie> {
        return remoteDataSource.getPopularMovies().results.map { it.toDomainMovie() }
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        val id = title.toIntOrNull() ?: return null
        // Temporary bridge: detail still uses id until title flow is implemented.
        return remoteDataSource.getMovieDetails(id).toDomainMovie()
    }
}
