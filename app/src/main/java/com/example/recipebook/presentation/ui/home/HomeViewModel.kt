package com.example.recipebook.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.usecase.categoryUseCase.GetCategoriesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.GetAllRecipesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.LoadMoreRecipesUseCase
import com.example.recipebook.domain.usecase.recipeUseCase.ToggleFavoriteUseCase
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
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val loadMoreRecipesUseCase: LoadMoreRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState.asStateFlow()

    private val _recipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Loading)
    val recipesState: StateFlow<UiState<List<Recipe>>> = _recipesState.asStateFlow()

    private val _events = Channel<HomeEvent>(BUFFERED)
    val events = _events.receiveAsFlow()

    private var currentOffset = 0
    private var isLoadingMore = false
    private var hasReachedEnd = false

    init {
        loadRecipes()
        loadCategories()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            try {
                getAllRecipesUseCase().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _recipesState.value = UiState.Loading
                        is Resource.Success -> {
                            val recipes = resource.data ?: emptyList()
                            _recipesState.value = if (recipes.isEmpty()) UiState.Empty
                            else UiState.Success(recipes)
                        }
                        is Resource.Error -> {
                            _recipesState.value = UiState.Error(
                                resource.message ?: "Unknown error",
                                resource.data
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _recipesState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
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
        _events.trySend(HomeEvent.NavigateToCategory(category.name))
    }

    fun onFavouriteClick(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe.id)
        }
    }

    fun loadMoreRecipes() {
        if (isLoadingMore || hasReachedEnd) return
        viewModelScope.launch {
            isLoadingMore = true
            try {
                currentOffset += 20
                val loadedCount = loadMoreRecipesUseCase(currentOffset)
                if (loadedCount == 0) {
                    hasReachedEnd = true
                    currentOffset -= 20
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                hasReachedEnd = true
                currentOffset -= 20
                _recipesState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            } finally {
                isLoadingMore = false
            }
        }
    }

    sealed interface HomeEvent {
        data class NavigateToCategory(val categoryName: String) : HomeEvent
    }
}