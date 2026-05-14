package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.testutils.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `deberia emitir secuencia de estados inicial loading data cuando remoto exitoso`() = runTest(mainDispatcherRule.dispatcher) {
        // Given
        val movies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                poster = "poster1",
                backdrop = "backdrop1",
                originalTitle = "Original 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 7.5
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                overview = "Overview 2",
                releaseDate = "2023-02-01",
                poster = "poster2",
                backdrop = null,
                originalTitle = "Original 2",
                originalLanguage = "es",
                popularity = 8.0,
                voteAverage = 6.0
            )
        )
        var useCaseCalled = 0
        val fakeUseCase = object : GetPopularMoviesUseCase {
            override suspend fun invoke(): List<QualifiedMovie> {
                useCaseCalled++
                yield()
                return movies.map { QualifiedMovie(it, it.voteAverage >= 6.0) }
            }
        }
        val viewModel = HomeViewModel(fakeUseCase)

        // When
        val states = mutableListOf<HomeViewModel.HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.take(3).toList(states) // initial, loading and data
        }

        runCurrent()
        viewModel.getAllMovies()
        advanceUntilIdle()
        collectJob.join()

        // Then
        assertEquals(3, states.size)
        assertFalse(states[0].isLoading)
        assertTrue(states[1].isLoading)
        assertFalse(states[2].isLoading)
        assertEquals(2, states[2].movies.size)
        assertEquals(1, useCaseCalled)
    }

    @Test
    fun `deberia emitir secuencia de estados con lista vacia cuando use case retorna vacio`() = runTest(mainDispatcherRule.dispatcher) {
        // Given
        var useCaseCalled = 0
        val fakeUseCase = object : GetPopularMoviesUseCase {
            override suspend fun invoke(): List<QualifiedMovie> {
                useCaseCalled++
                yield()
                return emptyList()
            }
        }
        val viewModel = HomeViewModel(fakeUseCase)

        // When
        val states = mutableListOf<HomeViewModel.HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.take(3).toList(states) // initial, loading and data
        }

        runCurrent()
        viewModel.getAllMovies()
        advanceUntilIdle()
        collectJob.join()

        // Then
        assertEquals(3, states.size)
        assertFalse(states[0].isLoading)
        assertTrue(states[1].isLoading)
        assertFalse(states[2].isLoading)
        assertTrue(states[2].movies.isEmpty())
        assertEquals(1, useCaseCalled)
    }

    @Test
    fun `deberia invocar use case una sola vez por llamada`() = runTest(mainDispatcherRule.dispatcher) {
        // Given
        var useCaseCalled = 0
        val fakeUseCase = object : GetPopularMoviesUseCase {
            override suspend fun invoke(): List<QualifiedMovie> {
                useCaseCalled++
                yield()
                return listOf(
                    QualifiedMovie(
                        Movie(
                            id = 1,
                            title = "Movie 1",
                            overview = "Overview 1",
                            releaseDate = "2023-01-01",
                            poster = "poster1",
                            backdrop = "backdrop1",
                            originalTitle = "Original 1",
                            originalLanguage = "en",
                            popularity = 10.0,
                            voteAverage = 7.5
                        ),
                        true
                    )
                )
            }
        }
        val viewModel = HomeViewModel(fakeUseCase)

        // When
        val collectJob = launch {
            viewModel.uiState.take(3).toList()
        }

        runCurrent()
        viewModel.getAllMovies()
        advanceUntilIdle()
        collectJob.join()

        // Then
        assertEquals(1, useCaseCalled)
    }

    @Test
    fun `deberia manejar excepcion del use case y emitir lista vacia`() = runTest(mainDispatcherRule.dispatcher) {
        var calls = 0
        val throwingUseCase = object : GetPopularMoviesUseCase {
            override suspend fun invoke(): List<QualifiedMovie> {
                calls++
                yield()
                throw IllegalStateException("boom")
            }
        }
        val vm = HomeViewModel(throwingUseCase)

        val states = mutableListOf<HomeViewModel.HomeUiState>()
        val collectJob = launch {
            vm.uiState.take(3).toList(states)
        }

        runCurrent()
        vm.getAllMovies()
        advanceUntilIdle()
        collectJob.join()

        // En caso de excepcion, el ViewModel debe emitir lista vacia y isLoading false
        assertEquals(3, states.size)
        assertFalse(states[2].isLoading)
        assertTrue(states[2].movies.isEmpty())
        assertEquals(1, calls)
    }
}