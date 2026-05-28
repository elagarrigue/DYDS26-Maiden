package edu.dyds.movies.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase

class DetailViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: Flow<DetailUiState> = _uiState

    fun getMovieDetail(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            _uiState.emit(DetailUiState(isLoading = true))
            val movie = try {
                getMovieDetailsUseCase(title)
            } catch (e: Exception) {
                null
            }
            _uiState.emit(DetailUiState(isLoading = false, movie = movie))
        }
    }

    data class DetailUiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
    )
}