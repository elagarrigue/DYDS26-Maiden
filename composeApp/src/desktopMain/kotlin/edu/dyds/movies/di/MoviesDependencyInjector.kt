package edu.dyds.movies.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.movies.data.MoviesRepositoryImpl
import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.data.external.broker.MovieExternalSourceBroker
import edu.dyds.movies.data.external.omdb.OMDBMoviesExternalSource
import edu.dyds.movies.data.external.tmdb.TMDBMoviesExternalSource
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.data.local.LocalDataSourceImpl
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCaseImpl
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCaseImpl
import edu.dyds.movies.presentation.detail.DetailViewModel
import edu.dyds.movies.presentation.home.HomeViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val API_KEY = "d18da1b5da16397619c688b0263cd281"
private const val OMDB_API_KEY = "91e9529d"

object MoviesDependencyInjector {

    private val tmdbHttpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.themoviedb.org"
                parameters.append("api_key", API_KEY)
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    private val omdbHttpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "www.omdbapi.com"
                parameters.append("apiKey", OMDB_API_KEY)
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    private val tmdbExternalSource: TMDBMoviesExternalSource by lazy { 
        TMDBMoviesExternalSource(tmdbHttpClient) 
    }

    private val omdbExternalSource: MovieExternalSource by lazy {
        OMDBMoviesExternalSource(omdbHttpClient)
    }

    private val movieExternalSourceBroker: MovieExternalSource by lazy {
        MovieExternalSourceBroker(tmdbExternalSource, omdbExternalSource)
    }

    private val moviesExternalSource: MoviesExternalSource by lazy { tmdbExternalSource }
    private val movieExternalSource: MovieExternalSource by lazy { movieExternalSourceBroker }

    private val localDataSource: LocalDataSource by lazy { LocalDataSourceImpl() }

    private val moviesRepository by lazy {
        MoviesRepositoryImpl(
            moviesExternalSource = moviesExternalSource,
            movieExternalSource = movieExternalSource,
            localDataSource = localDataSource
        )
    }

    private val getPopularMoviesUseCase: GetPopularMoviesUseCase by lazy {
        GetPopularMoviesUseCaseImpl(moviesRepository)
    }

    private val getMovieDetailsUseCase: GetMovieDetailsUseCase by lazy {
        GetMovieDetailsUseCaseImpl(moviesRepository)
    }

    @Composable
    fun getHomeViewModel(): HomeViewModel {
        return viewModel {
            HomeViewModel(
                getPopularMoviesUseCase
            )
        }
    }

    @Composable
    fun getDetailViewModel(): DetailViewModel {
        return viewModel {
            DetailViewModel(
                getMovieDetailsUseCase
            )
        }
    }
}
