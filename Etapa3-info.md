# Etapa 3: Cambios

**Requisitos de finalización**

**Cambio:** se desea integrar otro servicio (https://www.omdbapi.com/) para incrementar los datos del detalle de una película. Obtener la lista de películas no cambia. Esto implica:

- En vez de “get movie by id”, vamos a hacer “get movie by title”. El id de la lista de películas actual hace referencia al id de TMDB, lo cual no tiene correlación en OMDB. A su vez, ambos servicios soportan la búsqueda de películas por título.
- Lógica de construcción del objeto movie resultante:
  - Si ambos servicios retornan resultados, se crea un objeto movie con propiedades combinadas.
``` kotlin
private fun buildMovie(
    tmdbMovie: Movie.MovieItem,
    omdbMovie: Movie.MovieItem
) =

Movie.MovieItem(
    id = tmdbMovie.id,

    title = tmdbMovie.title,

    overview = "TMDB: ${tmdbMovie.overview}\n\nOMDB: ${omdbMovie.overview}",

    releaseDate = tmdbMovie.releaseDate,

    poster = tmdbMovie.poster,

    backdrop = tmdbMovie.backdrop,

    originalTitle = tmdbMovie.originalTitle,

    originalLanguage = tmdbMovie.originalLanguage,

    popularity = (tmdbMovie.popularity + omdbMovie.popularity) / 2.0,

    voteAverage = (tmdbMovie.voteAverage + omdbMovie.voteAverage) / 2.0
)
```
  - Si sólo uno de los servicios retorna un resultado, retornar ese resultado. Modificar el overview agregando el string “TMDB: “ u “OMDB: ” según corresponda.
  - Si ninguno retorna un resultado, retornar vacío.

---

## Tareas (implementar en distintos commits):

1. **Reemplazar TMDB get movie details by id por search movie by title.** Incluye:
   - Actualizar los tests
``` Claro, aquí tienes el contenido de las imágenes convertido a bloques de código Kotlin en Markdown:

```kotlin
interface MoviesExternalSource {
    suspend fun getPopularMovies(): List<MovieItem>
    suspend fun getMovieById(id: Int): MovieItem
    suspend fun getMovieByTitle(title: String): MovieItem
}
```

```kotlin
@Serializable
data class RemoteMovie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("poster_path") val posterPath: String,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("original_language") val originalLanguage: String,
    val popularity: Double,
    @SerialName("vote_average") val voteAverage: Double,
    val popularity: Double?,
    @SerialName("vote_average") val voteAverage: Double?
)
```

```kotlin
fun toDomainMovie() = Movie.MovieItem(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate,
    releaseDate = releaseDate ?: "",
    poster = "${TMDB_IMAGE_BASE_URL}/w185$posterPath",
    originalTitle = originalTitle,
    originalLanguage = originalLanguage,
    popularity = popularity,
    voteAverage = voteAverage,
    popularity = popularity ?: 0.0,
    voteAverage = voteAverage ?: 0.0
)
```

```kotlin
internal class TMDBMoviesExternalSourceImpl(
    private val tmdbHttpClient: HttpClient,
) : MoviesExternalSource {

    override suspend fun getPopularMovies(): List<MovieItem> =
        getTMDBMovies().results.map { it.toDomainMovie() }

    override suspend fun getMovieById(id: Int): MovieItem =
        getTMDBMovieDetails(id).toDomainMovie()

    override suspend fun getMovieByTitle(title: String): MovieItem =
        getTMDBMovieDetails(title).apply { println(this) }.results.first().toDomainMovie()

    private suspend fun getTMDBMovies(): RemoteResult =
        tmdbHttpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    private suspend fun getTMDBMovieDetails(id: Int): RemoteMovie =
        tmdbHttpClient.get("/3/movie/$id").body()

    private suspend fun getTMDBMovieDetails(title: String): RemoteResult =
        tmdbHttpClient.get("/3/search/movie?query=$title").body()
}
```

```kotlin
composable(HOME) {
    HomeScreen(
        ViewModel = getHomeViewModel(),
        onGoodMovieClick = {
            navController.navigate("$DETAIL/${it.id}")
            navController.navigate("$DETAIL/${it.title}")
        }
    )
}

composable(
    route = "$DETAIL/${MOVIE_ID}",
    arguments = listOf(navArgument(MOVIE_ID) { type = NavType.IntType })
) { backstackEntry ->
    val movieId = backstackEntry.arguments?.getInt(MOVIE_ID)
}

composable(
    route = "$DETAIL/${MOVIE_TITLE}",
    arguments = listOf(navArgument(MOVIE_TITLE) { type = NavType.StringType })
) { backstackEntry ->
    val movieId = backstackEntry.arguments?.getString(MOVIE_TITLE)
}
```

2. **Mover** la implementación de `MoviesExternalSource` a `data/external/tmdb`. Renombrar `MoviesExternalSourceImpl` a `TMDBMoviesExternalSource`.
3. **OMDB** sólo va a ser utilizado para obtener el detalle de una película, no una lista de películas. Aparece el incumplimiento del principio IS en `MoviesExternalSource`, **separar la interfaz en dos**.
Claro, aquí tienes el contenido convertido a bloques de código Kotlin en Markdown:

```kotlin
interface MoviesExternalSource {
    suspend fun getPopularMovies(): List<MovieItem>
}

interface MovieExternalSource {
    suspend fun getMovieByTitle(title: String): MovieItem
}
```
   - Actualizar los tests
 - 
4. **Agregar el servicio OMDB.** En este paso se puede reemplazar TMDB por OMDB para obtener la película por título.
```kotlin
private val omdbHttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
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
```

```kotlin
@Serializable
data class RemoteMovie(
    @SerialName(value = "Title") val title: String,
    @SerialName(value = "Plot") val plot: String,
    @SerialName(value = "Released") val released: String,
    @SerialName(value = "Year") val year: String,
    @SerialName(value = "Poster") val poster: String,
    @SerialName(value = "Language") val language: String,
    @SerialName(value = "Metascore") val metaScore: String,
    val imdbRating: Double,
)

fun toDomainMovie() = Movie.MovieItem(
    id = title.hashCode(),
    title = title,
    overview = plot,
    poster = poster,
    backdrop = poster,
    originalTitle = title,
    originalLanguage = language,
    popularity = imdbRating,
    voteAverage = if (metaScore.isNotEmpty() && metaScore != "N/A") metaScore.toDouble() else 0.0
)
```

```kotlin
package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class OMDBMoviesExternalSource(
    private val omdbHttpClient: HttpClient,
) : MovieExternalSource {

    override suspend fun getMovieByTitle(title: String): Movie.MovieItem =
        getOMDBMovieDetails(title).toDomainMovie()

    private suspend fun getOMDBMovieDetails(title: String): RemoteMovie =
        omdbHttpClient.get(urlString = "/?t=$title").body()
}
```
5. **Agregar un Broker** que implemente `MovieExternalSource` que dependa de `TMDBMoviesExternalSource` y `OMDBMoviesExternalSource` que implemente la lógica del requerimiento.
6. **Implementar los tests** para cubrir los casos del Broker.