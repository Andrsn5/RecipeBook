package com.example.recipebook.presentation.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.usecase.categoryUseCase.GetCategoriesUseCase
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState.asStateFlow()

    private val _events = Channel<CategoriesEvent>(BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                getCategoriesUseCase().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _categoriesState.value = UiState.Loading
                        is Resource.Success -> {
                            val categories = resource.data ?: emptyList()
                            _categoriesState.value = if (categories.isEmpty()) UiState.Empty
                            else UiState.Success(categories)
                        }
                        is Resource.Error -> {
                            _categoriesState.value = UiState.Error(
                                resource.message ?: "Unknown error",
                                resource.data
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _categoriesState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _events.trySend(CategoriesEvent.NavigateToRecipes(category.name))
    }

    sealed interface CategoriesEvent {
        data class NavigateToRecipes(val categoryName: String) : CategoriesEvent
    }
}