package com.example.recipebook.presentation.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.GetCategoriesUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
): ViewModel(){

    private val _state = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val state:MutableStateFlow<UiState<List<Recipe>>> = _state

    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onEach { category ->
                    if (category == null) _state.value = UiState.Empty
                    else _state.value = UiState.Success(category)
                }
                .catch { e->
                    _state.value = UiState.Error(e.message ?: "Error loading categories")
                }
                .collect()
        }
    }


}