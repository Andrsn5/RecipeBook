package com.example.recipebook.presentation.ui.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.usecase.categoryUseCase.GetCategoriesUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState = _categoriesState.asStateFlow()

    private var _selectedCategoryId: Int? = null
    val selectedCategoryId get() = _selectedCategoryId

    init {
        loadCategories()
    }

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
                        _categoriesState.value =
                            UiState.Error(resource.message ?: "Error loading categories")
                    }
                }
            }
        }
    }
    fun onCategorySelected(category: Category) {
        _selectedCategoryId = category.id
        // Здесь можно добавить фильтрацию рецептов по категории

    }
}