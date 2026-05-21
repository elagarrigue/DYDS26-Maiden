package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.presentation.fakes.FakeGetMovieDetailsUseCase
import edu.dyds.movies.presentation.fakes.ThrowingGetMovieDetailsUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    private lateinit var expectedMovie: Movie
    private lateinit var useCase: FakeGetMovieDetailsUseCase
    private lateinit var viewModel: DetailViewModel
    private lateinit var states: MutableList<DetailViewModel.DetailUiState>

    @Before
    fun setup() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        expectedMovie = buildMovie(id = 42)
        useCase = FakeGetMovieDetailsUseCase(result = expectedMovie)
        viewModel = DetailViewModel(useCase)
        states = mutableListOf()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emite estado inicial antes de cargar`() = runTest(dispatcher) {
        runGetMovieDetail(viewModel, 42, states)

        assertEquals(DetailViewModel.DetailUiState(), states[0])
    }

    @Test
    fun `emite loading al solicitar detalle`() = runTest(dispatcher) {
        runGetMovieDetail(viewModel, 42, states)

        assertTrue(states[1].isLoading)
        assertNull(states[1].movie)
    }

    @Test
    fun `emite movie cuando use case responde`() = runTest(dispatcher) {
        runGetMovieDetail(viewModel, 42, states)

        assertEquals(expectedMovie, states[2].movie)
        assertEquals(false, states[2].isLoading)
    }

    @Test
    fun `invoca use case una vez con id`() = runTest(dispatcher) {
        runGetMovieDetail(viewModel, 42, states)

        assertEquals(1, useCase.calls)
        assertEquals(42, useCase.lastRequestedId)
    }

    @Test
    fun `emite movie null cuando use case retorna null`() = runTest(dispatcher) {
        val useCase = FakeGetMovieDetailsUseCase(result = null)
        val viewModel = DetailViewModel(useCase)

        val states = mutableListOf<DetailViewModel.DetailUiState>()
        runGetMovieDetail(viewModel, 7, states)

        assertNull(states[2].movie)
        assertEquals(false, states[2].isLoading)
    }

    @Test
    fun `invoca use case una vez con id cuando retorna null`() = runTest(dispatcher) {
        val useCase = FakeGetMovieDetailsUseCase(result = null)
        val viewModel = DetailViewModel(useCase)

        val states = mutableListOf<DetailViewModel.DetailUiState>()
        runGetMovieDetail(viewModel, 7, states)

        assertEquals(1, useCase.calls)
        assertEquals(7, useCase.lastRequestedId)
    }

    @Test
    fun `emite movie null cuando use case lanza excepcion`() = runTest(dispatcher) {
        val throwingUseCase = ThrowingGetMovieDetailsUseCase()
        val vm = DetailViewModel(throwingUseCase)

        val localStates = mutableListOf<DetailViewModel.DetailUiState>()
        runGetMovieDetail(vm, 99, localStates)

        assertNull(localStates[2].movie)
        assertEquals(false, localStates[2].isLoading)
    }

    private suspend fun TestScope.runGetMovieDetail(
        viewModel: DetailViewModel,
        id: Int,
        states: MutableList<DetailViewModel.DetailUiState>,
    ) {
        val collectJob = launch {
            viewModel.uiState.take(3).toList(states)
        }

        viewModel.getMovieDetail(id)

        collectJob.join()
        viewModel.viewModelScope.coroutineContext[Job]?.cancelAndJoin()
    }

    private fun buildMovie(id: Int): Movie {
        return Movie(
            id = id,
            title = "Pelicula $id",
            overview = "Overview $id",
            releaseDate = "2024-01-01",
            poster = "https://image.tmdb.org/t/p/w185/poster.jpg",
            backdrop = "https://image.tmdb.org/t/p/w780/backdrop.jpg",
            originalTitle = "Original $id",
            originalLanguage = "en",
            popularity = 7.5,
            voteAverage = 6.7
        )
    }

}
