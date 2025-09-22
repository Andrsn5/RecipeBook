package com.example.recipebook.presentation.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.GetFavoritesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val favoritesState = _favoritesState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _favoritesState.value = UiState.Loading
                    is Resource.Success -> {
                        val favorites = resource.data ?: emptyList()
                        _favoritesState.value = if (favorites.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(favorites)
                        }
                    }
                    is Resource.Error -> {
                        _favoritesState.value = UiState.Error(resource.message ?: "Ошибка загрузки избранного")
                    }
                }
            }
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe.id)
            // Перезагружаем список после изменения
            loadFavorites()
        }
    }
}