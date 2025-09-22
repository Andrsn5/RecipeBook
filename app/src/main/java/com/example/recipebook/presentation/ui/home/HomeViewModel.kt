package com.example.recipebook.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.recipeUseCase.GetAllRecipesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val state: MutableStateFlow<UiState<List<Recipe>>> = _state

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            getAllRecipesUseCase()
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = UiState.Loading
                        is Resource.Success -> {
                            val data = resource.data
                            if (data.isNullOrEmpty()) {
                                _state.value = UiState.Empty
                            } else {
                                _state.value = UiState.Success(data)
                            }
                        }
                        is Resource.Error -> _state.value = UiState.Error(resource.message ?: "Unknown error")
                    }
                }
                .collect()
        }
    }

    private var currentCategory: String? = null


    fun filterByCategory(category: String) {
        currentCategory = category
        loadRecipes()
    }

    fun toggleFavorite(id: Int){
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
        }
    }
}