package com.example.recipebook.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.GetRecipeDetailsUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Recipe>>(UiState.Loading)
    val state: StateFlow<UiState<Recipe>> = _state.asStateFlow()

    val currentRecipe: Recipe?
        get() = (_state.value as? UiState.Success)?.data

    fun loadRecipe(id: Int) {
        viewModelScope.launch {
            try {
                getRecipeDetailsUseCase(id).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = UiState.Loading
                        is Resource.Success -> {
                            val recipe = resource.data
                            if (recipe != null) {
                                _state.value = UiState.Success(recipe)
                            } else {
                                _state.value = UiState.Empty
                            }
                        }
                        is Resource.Error -> {
                            _state.value = UiState.Error(resource.message ?: "Unknown error")
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _state.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
        }
    }
}