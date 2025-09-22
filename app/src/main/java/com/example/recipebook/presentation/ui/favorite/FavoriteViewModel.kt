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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
): ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val state = _state

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase()
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = UiState.Loading
                        is Resource.Success -> {
                            val data = resource.data
                            data?.let { _state.value = (if (it.isEmpty()) UiState.Empty else UiState.Success(data))  }
                        }
                        is Resource.Error -> _state.value = UiState.Error(resource.message ?: "Error loading favorites")
                    }
                }
                .collect()
        }
    }
    fun toggleFavorite(id: Int){
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
            loadFavorites()
        }
    }

    }


