package edu.dyds.movies.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase

class HomeViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: Flow<HomeUiState> = _uiState

    fun getAllMovies() {
        viewModelScope.launch {
            _uiState.emit(HomeUiState(isLoading = true))
            val movies = try {
                getPopularMoviesUseCase()
            } catch (e: Exception) {
                emptyList<QualifiedMovie>()
            }
            _uiState.emit(HomeUiState(isLoading = false, movies = movies))
        }
    }

    data class HomeUiState(
        val isLoading: Boolean = false,
        val movies: List<QualifiedMovie> = emptyList(),
    )
}