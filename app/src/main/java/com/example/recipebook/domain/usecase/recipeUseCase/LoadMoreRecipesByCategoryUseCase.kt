package com.example.recipebook.domain.usecase.recipeUseCase

import com.example.recipebook.domain.repository.RecipeRepository
import javax.inject.Inject

class LoadMoreRecipesByCategoryUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(category: String, offset: Int): Int {
        return recipeRepository.loadMoreRecipesByCategory(category, offset)
    }
}
