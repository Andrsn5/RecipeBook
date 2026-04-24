package com.example.recipebook.presentation.ui.categoryRecipes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.GetRecipesByCategoryUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryRecipesViewModel @Inject constructor(
    private val getRecipesByCategoryUseCase: GetRecipesByCategoryUseCase,
    private val toggleFavouriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _recipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val recipesState: StateFlow<UiState<List<Recipe>>> = _recipesState

    private var currentCategory: String? = null

    init {
        val categoryName = savedStateHandle.get<String>("categoryName")
        if (categoryName != null) {
            currentCategory = categoryName
            loadRecipes(categoryName)
        } else {
            Log.e("CategoryRecipesViewModel", "Category name not found in SavedStateHandle")
            _recipesState.value = UiState.Error("Category not found")
        }
    }

    fun loadRecipes(category: String) {
        viewModelScope.launch {
            try {
                getRecipesByCategoryUseCase(category)
                    .onEach { resource ->
                        when (resource) {
                            is Resource.Loading -> _recipesState.value = UiState.Loading
                            is Resource.Success -> {
                                val data = resource.data
                                data?.let {
                                    _recipesState.value = if (it.isEmpty()) UiState.Empty else UiState.Success(data)
                                }
                            }
                            is Resource.Error -> {
                                Log.e("CategoryRecipesViewModel", "Error loading recipes: ${resource.message}")
                                _recipesState.value = UiState.Error(resource.message ?: "Error loading recipes")
                            }
                        }
                    }
                    .collect()
            } catch (e: Exception) {
                Log.e("CategoryRecipesViewModel", "Exception in loadRecipes: ${e.message}")
                _recipesState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun onFavouriteClick(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavouriteUseCase(recipe.id)
            val currentState = _recipesState.value
            if (currentState is UiState.Success) {
                val updated = currentState.data.map {
                    if (it.id == recipe.id) it.copy(favourite = !it.favourite) else it
                }
                _recipesState.value = UiState.Success(updated)
            }
        }
    }
}
