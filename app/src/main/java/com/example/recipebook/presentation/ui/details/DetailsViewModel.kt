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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
): ViewModel(){

    private val _state = MutableStateFlow<UiState<Recipe>>(UiState.Loading)
    val state:MutableStateFlow<UiState<Recipe>> = _state

    fun loadRecipe(id: String) {
        viewModelScope.launch {
            getRecipeDetailsUseCase(id)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = UiState.Loading
                        is Resource.Success -> {
                            val recipe = resource.data
                            _state.value = recipe?.let { UiState.Success(it) } ?: UiState.Empty
                        }
                        is Resource.Error -> _state.value = UiState.Error(resource.message ?: "Error loading")
                    }
                }
                .collect()
        }
    }

    fun toggleFavorite(id: String){
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
        }
    }
}