package com.example.recipebook.presentation.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.categoryUseCase.GetCategoriesUseCase
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

    private val _state = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val state:MutableStateFlow<UiState<List<Category>>> = _state

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = UiState.Loading
                        is Resource.Success -> {
                            val data = resource.data
                            _state.value = if (data.isNullOrEmpty()) UiState.Empty else UiState.Success(data)
                        }
                        is Resource.Error -> _state.value = UiState.Error(resource.message ?: "Error loading categories")
                    }
                }
                .collect()
        }
    }




}