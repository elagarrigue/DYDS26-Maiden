# Plan Etapa 3

## Assumptions and decisions
- Use title end-to-end (String) and keep method names but rename params to title.
- Normalized title = trim + collapse whitespace + lowercase(Locale.ROOT).
- Cache detail by normalizedTitle in memory, no TTL, do not cache nulls.
- TMDB search: exact match by normalized title, else first result, else null.
- OMDB DTO fields as String; map "N/A" to ""; parse ratings to Double with 0.0 fallback.
- Broker calls both sources in parallel and combines results per spec.
- Overview formatting: "TMDB: ...\n\nOMDB: ..." when both, "TMDB: ..." or "OMDB: ..." when only one.
- Broker mapping when both respond: base all fields from TMDB; overview combined; popularity/voteAverage averaged.
- OMDB mapping to Movie: id = title.hashCode(); title/originalTitle = Title; overview = Plot; releaseDate = Released (if not N/A/blank) else Year else ""; poster/backdrop = Poster ("" if N/A); originalLanguage = Language ("" if N/A); popularity = imdbRating; voteAverage = metascore.
- TMDB image base URLs stay as-is: poster = https://image.tmdb.org/t/p/w185{posterPath}, backdrop = https://image.tmdb.org/t/p/w780{backdropPath}; if posterPath null -> "", if backdropPath null -> null.

## Etapa 1: Refactor base y TMDB
### Etapa 1.1: Base e interfaces
1. Add normalization helper in data/TitleNormalization.kt with `internal fun normalizeTitle(title: String): String` using trim, `\\s+` collapse, and lowercase(Locale.ROOT).
2. Create MoviesExternalSource (popular) interface with signature `getPopularMovies(): List<Movie>`.
3. Create MovieExternalSource (detail) interface with signature `getMovieByTitle(title: String): Movie?`.
4. Replace RemoteDataSource usages with the new interfaces (no behavior change yet).

### Etapa 1.2: TMDB refactor
5. Move TMDB DTOs (RemoteMovie/RemoteResult) into data/external/tmdb.
6. Move TMDB implementation into data/external/tmdb and rename to TMDBMoviesExternalSource.
7. Make TMDBMoviesExternalSource implement both interfaces (popular + detail).
8. Update TMDB RemoteMovie fields to nullable (releaseDate, posterPath, backdropPath, popularity, voteAverage).
9. Update TMDB toDomainMovie defaults: releaseDate = "", popularity = 0.0, voteAverage = 0.0; preserve image base URLs.
10. Update TMDB popular flow to return List<Movie> directly using /3/discover/movie?sort_by=popularity.desc.
11. Implement TMDB search by title using /3/search/movie and parameter("query", title).
12. Add exact normalized title match in TMDB search; fallback to first result; return null when empty.

### Etapa 1.3: Flujo por titulo en dominio y UI
13. Update domain repository and use case signatures to take `title: String` (same method names).
14. Rename params and local vars from id to title across repo/use case/view model.
15. Update navigation to use MOVIE_TITLE String argument and pass Movie.title from the list.
16. Encode title with URLEncoder (UTF-8) on navigate and decode with URLDecoder (UTF-8) in destination.
17. Update DetailViewModel and DetailScreen to use title; LaunchedEffect(title).
18. Guard blank title before calling use case.

### Etapa 1.4: Cache, repo y DI
19. Extend LocalDataSource with getMovieDetail(normalizedTitle) and saveMovieDetail(normalizedTitle, movie).
20. Implement detail cache map in LocalDataSourceImpl.
21. Update MoviesRepositoryImpl constructor to accept MoviesExternalSource and MovieExternalSource.
22. Update getMovieDetails(title): guard blank, normalize, check detail cache, call source, save non-null, return null on exceptions.
23. Update dependency injector to use TMDBMoviesExternalSource and new interfaces.
24. Preserve TMDB client config in DI: host api.themoviedb.org, api_key param, ignoreUnknownKeys, timeout 5000ms.
25. Remove or replace old RemoteDataSource types and fakes (RemoteDataSource.kt/RemoteDataSourceImpl.kt, FakeRemoteDataSource) and update imports.

## Etapa 2: OMDB y broker
1. Add OMDB DTO in data/external/omdb with Response/Error and string fields.
2. Implement OMDB toDomainMovie mapping per OMDB rules (id hashCode, releaseDate fallback, poster/backdrop, ratings).
3. Implement OMDBMoviesExternalSource.getMovieByTitle using parameter("t", title); return null when Response != "True".
4. Add OMDB HttpClient in DI (separate from TMDB) with host www.omdbapi.com and parameter apiKey.
5. Preserve OMDB client config: ignoreUnknownKeys, timeout 5000ms.
6. Add MovieExternalSourceBroker in data/external/broker implementing MovieExternalSource.
7. In broker, call TMDB and OMDB in parallel with supervisorScope + async.
8. Handle per-source exceptions/timeouts by treating result as null.
9. Implement combination rules:
	- Both: base TMDB fields, overview "TMDB: ...\n\nOMDB: ...", popularity/voteAverage averaged.
	- Only TMDB: return TMDB with overview prefixed "TMDB: ".
	- Only OMDB: return OMDB with overview prefixed "OMDB: ".
	- None: return null.
10. Update DI to use MovieExternalSourceBroker for detail fetching.

## Etapa 3: Tests
1. Update repository popular tests to use MoviesExternalSource returning List<Movie>.
2. Update repository detail tests to pass title and assert cache by normalizedTitle.
3. Update fake data sources for new interfaces and title-based calls.
4. Update FakeMoviesRepository and use case tests to accept title.
5. Update DetailViewModel tests to call getMovieDetail(title) and assert lastRequestedTitle.
6. Add MovieExternalSourceBrokerTest for both/only TMDB/only OMDB/none cases.
7. Add a small test verifying title normalization in the detail cache.
8. Run ./gradlew test desktopTest (or the provided tasks) and fix failures.

## Files to touch (non-exhaustive)
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/TitleNormalization.kt (new)
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/external/MoviesExternalSource.kt (new)
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/external/MovieExternalSource.kt (new)
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/external/tmdb/* (moved/updated)
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/external/omdb/* (new)
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/external/broker/MovieExternalSourceBroker.kt (new)
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/local/LocalDataSource.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/local/LocalDataSourceImpl.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/data/MoviesRepositoryImpl.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/domain/repository/MoviesRepository.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/domain/usecase/GetMovieDetailsUseCase.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/presentation/Navigation.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/presentation/detail/DetailScreen.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/presentation/detail/DetailViewModel.kt
- composeApp/src/desktopMain/kotlin/edu/dyds/movies/di/MoviesDependencyInjector.kt
- composeApp/src/desktopTest/kotlin/edu/dyds/movies/** (update + new broker tests)
