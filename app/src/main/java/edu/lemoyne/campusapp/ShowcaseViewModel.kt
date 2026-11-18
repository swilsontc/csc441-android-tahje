package edu.lemoyne.campusapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

sealed class ShowcaseUIState {
    data object Success : ShowcaseUIState()
    data class Success(val items: List<ShowcaseItem>) : ShowcaseUiState()
    data class Error(val message: String) : ShowcaseUIState()

}

class ShowcaseViewModel : ViewModel() {
    private val repository = ShowcaseRepository()
    private val _uiState = MutableStateFlow<ShowcaseUiState>(ShowcaseUIState.Loading)
    val uiState: StateFlow<ShowcaseUiState> = _uiState

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ShowcaseUiState.Loading
            try {
                val items = repository.loadItems()
                _uiState.value = ShowaseUiState.Success(items)

            } catch (error: Exception) {
                _uiState.value = ShowaseUiState.Error(error.message ?: "Network error")
            }
        }
    }

}