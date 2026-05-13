# Plan Etapa 2 - Issues (3 stages)

## Criterios globales (aplican a todas las issues)
- Fakes manuales por archivo (autocontenidos, sin librerias de mocking).
- Nombres de tests en espanol (backticks).
- Asserts de kotlin.test (assertEquals, assertTrue, assertNull).
- JUnit4 para ejecucion (@Test) con kotlin-test-junit.
- Usar runTest para tests suspend (repository y use cases).
- En ViewModels: usar StandardTestDispatcher, Dispatchers.setMain/resetMain con MainDispatcherRule, y runTest con el dispatcher de la regla.
- En ViewModels: usar advanceUntilIdle() y capturar secuencia con uiState.take(3).toList().
- No validar guardas de re-entrada (no existen en el codigo).

## Stage 1 - Infraestructura de tests

1) Configurar dependencias de test y limpiar ejemplos
- Type: AFK
- Blocked by: None - can start immediately
- User stories covered: N/A
- Estado: resuelto

### Issue 1 body
## What to build
Preparar el source set desktopTest con dependencias de test y eliminar el archivo de ejemplo. Dejar el entorno listo para correr unit tests con JUnit4 y coroutines test.

## Acceptance criteria
- [x] desktopTest tiene dependencias de test: kotlin-test, kotlin-test-junit, kotlinx-coroutines-test y junit.
- [x] TestExample.kt se elimina o se reemplaza (no queda contenido de ejemplo).
- [x] Los tests pueden usar kotlin.test y @Test de JUnit4 sin errores de dependencias.

## Blocked by
None - can start immediately

---

2) Agregar MainDispatcherRule para ViewModels
- Type: AFK
- Blocked by: Issue 1
- User stories covered: N/A
- Estado: resuelto

### Issue 2 body
## What to build
Crear una regla reutilizable para controlar Dispatchers.Main en tests de ViewModel, usando StandardTestDispatcher y coroutines test.

## Acceptance criteria
- [x] MainDispatcherRule existe en el paquete edu.dyds.movies.testutils.
- [x] La regla hace set/reset de Dispatchers.Main.
- [x] La regla expone un dispatcher reutilizable para runTest.

## Blocked by
- Issue 1

---

## Stage 2 - Slice: Popular movies (lista)
Estado: saltado por trabajo en paralelo (otro desarrollador).

3) Popular movies - remoto exitoso
- Type: AFK
- Blocked by: Issue 1, Issue 2
- User stories covered: N/A

### Issue 3 body
## What to build
Tests unitarios del camino remoto exitoso de peliculas populares: repository mapea RemoteMovie a Movie y guarda cache; use case ordena y setea isGoodMovie; HomeViewModel publica estados correctos.

## Acceptance criteria
- [ ] MoviesRepositoryImpl.getPopularMovies (remoto exitoso):
  - [ ] mapea RemoteMovie a Movie.
  - [ ] saveMovies recibe la lista mapeada y se llama una sola vez.
  - [ ] poster/backdrop usan prefijos w185/w780.
  - [ ] backdropPath = null -> backdrop null.
  - [ ] mapeo validado indirectamente (sin test dedicado a toDomainMovie).
- [ ] GetPopularMoviesUseCaseImpl:
  - [ ] ordena por voteAverage desc.
  - [ ] isGoodMovie usa umbral 6.0 (incluye borde 6.0 == true).
  - [ ] preserva todos los elementos (mismo tamano y ids).
- [ ] HomeViewModel:
  - [ ] secuencia completa (inicial -> loading -> data) con uiState.take(3).toList() y advanceUntilIdle().
  - [ ] use case se invoca una vez por llamada.
- [ ] Fakes manuales por archivo; nombres en espaniol; asserts kotlin.test; runTest con dispatcher de MainDispatcherRule.

## Blocked by
- Issue 1
- Issue 2

---

4) Popular movies - cache hit
- Type: AFK
- Blocked by: Issue 1, Issue 2
- User stories covered: N/A

### Issue 4 body
## What to build
Tests unitarios del camino de cache para peliculas populares: LocalDataSourceImpl almacena y retorna lista; MoviesRepositoryImpl retorna cache sin llamar remoto; HomeViewModel publica estados con lista cacheada (via fake use case).

## Acceptance criteria
- [ ] LocalDataSourceImpl:
  - [ ] inicia vacio.
  - [ ] saveMovies actualiza cache con lista de 2 Movie (assertEquals).
- [ ] MoviesRepositoryImpl.getPopularMovies (cache hit):
  - [ ] retorna exactamente la lista cacheada (sin reordenar ni mapear).
  - [ ] remoto no se llama (contador en fake remoto).
- [ ] HomeViewModel:
  - [ ] secuencia completa (inicial -> loading -> data) con lista cacheada.
  - [ ] use case se invoca una vez por llamada.
- [ ] Fakes manuales por archivo; nombres en espaniol; asserts kotlin.test; runTest con dispatcher de MainDispatcherRule.

## Blocked by
- Issue 1
- Issue 2

---

5) Popular movies - error remoto
- Type: AFK
- Blocked by: Issue 1, Issue 2
- User stories covered: N/A

### Issue 5 body
## What to build
Tests unitarios del camino de error en peliculas populares: repository retorna lista vacia cuando remoto falla; use case devuelve lista vacia; HomeViewModel termina con movies vacio e isLoading false.

## Acceptance criteria
- [ ] MoviesRepositoryImpl.getPopularMovies (remoto falla) retorna lista vacia.
- [ ] GetPopularMoviesUseCaseImpl devuelve lista vacia cuando el repo devuelve vacio.
- [ ] HomeViewModel:
  - [ ] secuencia completa (inicial -> loading -> data).
  - [ ] estado final con movies vacio e isLoading = false.
- [ ] Fakes manuales por archivo; nombres en espaniol; asserts kotlin.test; runTest con dispatcher de MainDispatcherRule.

## Blocked by
- Issue 1
- Issue 2

---

## Stage 3 - Slice: Movie details (detalle)

6) Movie details - cache hit
- Type: AFK
- Blocked by: Issue 1, Issue 2
- User stories covered: N/A

### Issue 6 body
## What to build
Tests unitarios del camino cacheado para detalle de pelicula: repository retorna movie cacheado sin remoto; use case delega; DetailViewModel publica estados correctos.

## Acceptance criteria
- [ ] MoviesRepositoryImpl.getMovieDetails (cache hit):
  - [ ] retorna movie cacheado.
  - [ ] remoto no se llama (contador en fake remoto).
- [ ] GetMovieDetailsUseCaseImpl delega y retorna Movie.
- [ ] DetailViewModel:
  - [ ] secuencia completa (inicial -> loading -> data) con uiState.take(3).toList().
  - [ ] use case se invoca una vez por llamada.
- [ ] Fakes manuales por archivo; nombres en espaniol; asserts kotlin.test; runTest con dispatcher de MainDispatcherRule.

## Blocked by
- Issue 1
- Issue 2

---

7) Movie details - remoto exitoso
- Type: AFK
- Blocked by: Issue 1, Issue 2
- User stories covered: N/A

### Issue 7 body
## What to build
Tests unitarios del camino remoto exitoso para detalle de pelicula: repository llama remoto con id correcto y mapea a dominio; use case delega; DetailViewModel publica estados correctos.

## Acceptance criteria
- [ ] MoviesRepositoryImpl.getMovieDetails (cache vacio):
  - [ ] llama remoto con id correcto.
  - [ ] retorna Movie mapeado.
- [ ] GetMovieDetailsUseCaseImpl delega y retorna Movie.
- [ ] DetailViewModel:
  - [ ] secuencia completa (inicial -> loading -> data).
  - [ ] use case se invoca una vez por llamada.
- [ ] Fakes manuales por archivo; nombres en espaniol; asserts kotlin.test; runTest con dispatcher de MainDispatcherRule.

## Blocked by
- Issue 1
- Issue 2

---

8) Movie details - error remoto
- Type: AFK
- Blocked by: Issue 1, Issue 2
- User stories covered: N/A

### Issue 8 body
## What to build
Tests unitarios del camino de error en detalle de pelicula: repository retorna null cuando remoto falla; use case retorna null; DetailViewModel termina con movie null e isLoading false.

## Acceptance criteria
- [ ] MoviesRepositoryImpl.getMovieDetails (remoto falla) retorna null.
- [ ] GetMovieDetailsUseCaseImpl retorna null cuando el repo devuelve null.
- [ ] DetailViewModel:
  - [ ] secuencia completa (inicial -> loading -> data).
  - [ ] estado final con movie null e isLoading = false.
- [ ] Fakes manuales por archivo; nombres en espaniol; asserts kotlin.test; runTest con dispatcher de MainDispatcherRule.

## Blocked by
- Issue 1
- Issue 2
