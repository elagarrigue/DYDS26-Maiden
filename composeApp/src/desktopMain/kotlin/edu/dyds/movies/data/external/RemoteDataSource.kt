package edu.dyds.movies.data.external

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

interface RemoteDataSource {
    suspend fun getPopularMovies(): RemoteResult
    suspend fun getMovieDetails(id: Int): RemoteMovie
}
