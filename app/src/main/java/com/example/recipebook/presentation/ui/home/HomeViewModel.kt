package com.example.recipebook.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.categoryUseCase.GetCategoriesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.GetAllRecipesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState = _categoriesState.asStateFlow()

    private val _recipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val recipesState = _recipesState.asStateFlow()

    private var _selectedCategoryId: Int? = null
    val selectedCategoryId get() = _selectedCategoryId

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _categoriesState.value = UiState.Loading
                    is Resource.Success -> {
                        val categories = resource.data ?: emptyList()
                        _categoriesState.value = if (categories.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(categories)
                        }
                    }
                    is Resource.Error -> {
                        _categoriesState.value = UiState.Error(resource.message ?: "Error loading categories")
                    }
                }
            }
        }
    }

    fun loadRecipes() {
        viewModelScope.launch {
            getAllRecipesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _recipesState.value = UiState.Loading
                    is Resource.Success -> {
                        val recipes = resource.data ?: emptyList()
                        _recipesState.value = if (recipes.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(recipes)
                        }
                    }
                    is Resource.Error -> {
                        _recipesState.value = UiState.Error(resource.message ?: "Error loading recipes")
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _selectedCategoryId = category.id
        // Здесь можно добавить фильтрацию рецептов по категории
        loadRecipes() // Перезагружаем рецепты с учетом выбранной категории
    }

    fun onFavouriteClick(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe.id)
            loadRecipes()
        }
    }
}