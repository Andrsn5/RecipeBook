package com.example.recipebook.presentation.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.GetFavoritesUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase
): ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val state = _state

    init {
        loadFavorite()
    }

    private fun loadFavorite() {
        viewModelScope.launch {
            getFavoritesUseCase()
                .onEach { recipes ->
                    if (recipes.isEmpty()) {
                        _state.value = UiState.Empty
                    } else {
                        _state.value = UiState.Success(recipes)
                    }
                }
                .catch { e->
                    _state.value = UiState.Error(e.localizedMessage ?: "Error loading favorites")
                }
                .collect()
            }
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is favorite Fragment"
    }
    val text: LiveData<String> = _text
