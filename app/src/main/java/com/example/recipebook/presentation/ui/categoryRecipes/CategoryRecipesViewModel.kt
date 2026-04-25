package com.example.recipebook.presentation.ui.categoryRecipes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.GetRecipesByCategoryUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.LoadMoreRecipesByCategoryUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CategoryRecipesViewModel @Inject constructor(
    private val getRecipesByCategoryUseCase: GetRecipesByCategoryUseCase,
    private val loadMoreRecipesByCategoryUseCase: LoadMoreRecipesByCategoryUseCase,
    private val toggleFavouriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _recipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val recipesState: StateFlow<UiState<List<Recipe>>> = _recipesState.asStateFlow()

    private val currentCategory: String?
    private var currentOffset = 0
    private var isLoadingMore = false

    init {
        currentCategory = savedStateHandle.get<String>("categoryName")
        if (currentCategory != null) {
            loadRecipes(currentCategory)
        } else {
            Log.e("CategoryRecipesViewModel", "Category name not found in SavedStateHandle")
            _recipesState.value = UiState.Error("Category not found")
        }
    }

    fun loadRecipes(category: String) {
        viewModelScope.launch {
            try {
                getRecipesByCategoryUseCase(category).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _recipesState.value = UiState.Loading
                        is Resource.Success -> {
                            val data = resource.data ?: emptyList()
                            _recipesState.value = if (data.isEmpty()) UiState.Empty
                            else UiState.Success(data)
                        }
                        is Resource.Error -> {
                            Log.e("CategoryRecipesViewModel", "Error: ${resource.message}")
                            _recipesState.value = UiState.Error(
                                resource.message ?: "Unknown error",
                                resource.data
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("CategoryRecipesViewModel", "Exception: ${e.message}")
                _recipesState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun onFavouriteClick(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavouriteUseCase(recipe.id)
        }
    }

    fun loadMoreRecipes() {
        val category = currentCategory ?: return
        if (isLoadingMore) return
        viewModelScope.launch {
            isLoadingMore = true
            try {
                currentOffset += 20
                loadMoreRecipesByCategoryUseCase(category, currentOffset)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                currentOffset -= 20
                _recipesState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            } finally {
                isLoadingMore = false
            }
        }
    }
}