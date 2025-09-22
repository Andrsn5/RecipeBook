package com.example.recipebook.presentation.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.GetAllRecipesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val favoritesState = _favoritesState.asStateFlow()





    fun loadFavorites() {
        viewModelScope.launch {
            getAllRecipesUseCase().collect { resource ->

                when (resource) {
                    is Resource.Loading -> {
                        _favoritesState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        val allRecipes = resource.data ?: emptyList()

                        allRecipes.forEachIndexed { index, recipe ->
                        }
                        val favorites = allRecipes.filter { it.favourite }
                        favorites.forEachIndexed { index, recipe ->
                        }

                        _favoritesState.value = if (favorites.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(favorites)
                        }
                    }
                    is Resource.Error -> {
                        _favoritesState.value = UiState.Error(resource.message ?: "Ошибка загрузки")
                    }
                }
            }
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe.id)

            kotlinx.coroutines.delay(300)
            loadFavorites()
        }
    }
}