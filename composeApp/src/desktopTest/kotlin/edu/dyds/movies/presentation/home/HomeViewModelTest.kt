package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.presentation.fakes.FakeGetMoviesUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Before
    fun setup() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deberia emitir secuencia de estados inicial loading data cuando remoto exitoso`() = runTest(dispatcher) {
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
        val fakeUseCase = FakeGetMoviesUseCase(
            result = movies.map { QualifiedMovie(it, it.voteAverage >= 6.0) }
        )
        val viewModel = HomeViewModel(fakeUseCase)

        // When
        val states = mutableListOf<HomeViewModel.HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.take(3).toList(states) // initial, loading and data
        }

        viewModel.getAllMovies()
        collectJob.join()

        // Then
        assertEquals(3, states.size)
        assertFalse(states[0].isLoading)
        assertTrue(states[1].isLoading)
        assertFalse(states[2].isLoading)
        assertEquals(2, states[2].movies.size)
        assertEquals(1, fakeUseCase.calls)
    }

    @Test
    fun `deberia emitir secuencia de estados con lista vacia cuando use case retorna vacio`() = runTest(dispatcher) {
        // Given
        val fakeUseCase = FakeGetMoviesUseCase(result = emptyList())
        val viewModel = HomeViewModel(fakeUseCase)

        // When
        val states = mutableListOf<HomeViewModel.HomeUiState>()
        val collectJob = launch {
            viewModel.uiState.take(3).toList(states) // initial, loading and data
        }

        viewModel.getAllMovies()
        collectJob.join()

        // Then
        assertEquals(3, states.size)
        assertFalse(states[0].isLoading)
        assertTrue(states[1].isLoading)
        assertFalse(states[2].isLoading)
        assertTrue(states[2].movies.isEmpty())
        assertEquals(1, fakeUseCase.calls)
    }

    @Test
    fun `deberia invocar use case una sola vez por llamada`() = runTest(dispatcher) {
        // Given
        val fakeUseCase = FakeGetMoviesUseCase(
            result = listOf(
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
        )
        val viewModel = HomeViewModel(fakeUseCase)

        // When
        val collectJob = launch {
            viewModel.uiState.take(3).toList()
        }

        viewModel.getAllMovies()
        collectJob.join()

        // Then
        assertEquals(1, fakeUseCase.calls)
    }

    @Test
    fun `deberia manejar excepcion del use case y emitir lista vacia`() = runTest(dispatcher) {
        val fakeUseCase = FakeGetMoviesUseCase(
            exceptionToThrow = IllegalStateException("boom")
        )
        val vm = HomeViewModel(fakeUseCase)

        val states = mutableListOf<HomeViewModel.HomeUiState>()
        val collectJob = launch {
            vm.uiState.take(3).toList(states)
        }

        vm.getAllMovies()
        collectJob.join()

        // En caso de excepcion, el ViewModel debe emitir lista vacia y isLoading false
        assertEquals(3, states.size)
        assertFalse(states[2].isLoading)
        assertTrue(states[2].movies.isEmpty())
        assertEquals(1, fakeUseCase.calls)
    }
}