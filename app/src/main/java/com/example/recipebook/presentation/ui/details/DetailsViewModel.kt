package com.example.recipebook.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.GetRecipeDetailsUseCase
import com.example.recipebook.domain.usecase.ToggleFavoriteUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
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

    fun loadRecipe(id: String){
        viewModelScope.launch {
            getRecipeDetailsUseCase(id)
                .onEach { recipe ->
                    if (recipe == null) _state.value = UiState.Empty
                    else _state.value = UiState.Success(recipe)
                }
                .catch { e->
                    _state.value = UiState.Error(e.message ?: "Error loading")
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