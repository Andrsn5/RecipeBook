package com.example.recipebook.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.SearchRecipesUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
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

    fun search(search: String){
        viewModelScope.launch {
            _state.value = UiState.Loading
            searchRecipesUseCase(query = search)
                .onEach { recipes ->
                    _state.value = if (recipes.isEmpty()) UiState.Empty else UiState.Success(recipes)
                }
                .catch { e ->
                    _state.value = UiState.Error(e.localizedMessage ?: "Error searching")
                }
                .collect()
        }
    }
}