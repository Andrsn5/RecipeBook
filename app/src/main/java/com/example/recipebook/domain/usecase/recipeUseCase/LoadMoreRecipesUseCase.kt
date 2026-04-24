package com.example.recipebook.domain.usecase.recipeUseCase

import com.example.recipebook.domain.repository.RecipeRepository
import javax.inject.Inject

class LoadMoreRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
){
    suspend operator fun invoke(offset: Int): Int = repository.loadMoreRecipes(offset)
}
