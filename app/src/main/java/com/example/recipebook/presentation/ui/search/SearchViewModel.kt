package com.example.recipebook.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.SearchRecipesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Recipe>>> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = UiState.Loading
            searchRecipesUseCase(query)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = UiState.Loading
                        is Resource.Success -> {
                            val data = resource.data ?: emptyList()
                            _state.value = if (data.isEmpty()) UiState.Empty
                            else UiState.Success(data)
                        }
                        is Resource.Error -> {
                            _state.value = UiState.Error(resource.message ?: "Error searching")
                        }
                    }
                }
        }
    }

    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipeId)
        }
    }
}