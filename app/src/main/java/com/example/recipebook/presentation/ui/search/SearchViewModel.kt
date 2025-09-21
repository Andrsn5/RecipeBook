package com.example.recipebook.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.SearchRecipesUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase
): ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val state : MutableStateFlow<UiState<List<Recipe>>> = _state

    fun search(query: String) {
        viewModelScope.launch {
            searchRecipesUseCase(query)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = UiState.Loading
                        is Resource.Success -> {
                            val data = resource.data
                            data?.let { _state.value = (if (it.isEmpty()) UiState.Empty else UiState.Success(data)) as UiState<List<Recipe>> }
                        }
                        is Resource.Error -> _state.value = UiState.Error(resource.message ?: "Error searching")
                    }
                }
                .collect()
        }
    }
}