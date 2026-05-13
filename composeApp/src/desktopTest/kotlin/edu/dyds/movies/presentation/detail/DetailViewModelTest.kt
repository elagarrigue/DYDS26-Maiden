package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import edu.dyds.movies.testutils.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `publica estados con movie cuando use case responde`() = runTest(mainDispatcherRule.dispatcher) {
        val expectedMovie = buildMovie(id = 42)
        val useCase = FakeGetMovieDetailsUseCase(result = expectedMovie)
        val viewModel = DetailViewModel(useCase)

        val statesDeferred = async { viewModel.uiState.take(3).toList() }

        viewModel.getMovieDetail(42)

        advanceUntilIdle()
        val states = statesDeferred.await()

        assertEquals(3, states.size)
        assertEquals(DetailViewModel.DetailUiState(), states[0])
        assertTrue(states[1].isLoading)
        assertNull(states[1].movie)
        assertEquals(expectedMovie, states[2].movie)
        assertEquals(false, states[2].isLoading)
        assertEquals(1, useCase.calls)
        assertEquals(42, useCase.lastRequestedId)
    }

    @Test
    fun `publica estados con movie null cuando use case falla`() = runTest(mainDispatcherRule.dispatcher) {
        val useCase = FakeGetMovieDetailsUseCase(result = null)
        val viewModel = DetailViewModel(useCase)

        val statesDeferred = async { viewModel.uiState.take(3).toList() }

        viewModel.getMovieDetail(7)

        advanceUntilIdle()
        val states = statesDeferred.await()

        assertEquals(3, states.size)
        assertEquals(DetailViewModel.DetailUiState(), states[0])
        assertTrue(states[1].isLoading)
        assertNull(states[1].movie)
        assertNull(states[2].movie)
        assertEquals(false, states[2].isLoading)
        assertEquals(1, useCase.calls)
        assertEquals(7, useCase.lastRequestedId)
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

    private class FakeGetMovieDetailsUseCase(
        private val result: Movie?
    ) : GetMovieDetailsUseCase {
        var calls = 0
        var lastRequestedId: Int? = null

        override suspend fun invoke(id: Int): Movie? {
            calls++
            lastRequestedId = id
            return result
        }
    }
}
