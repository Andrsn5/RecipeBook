package com.example.recipebook.domain.usecase.recipeUseCase

import com.example.recipebook.domain.repository.RecipeRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: RecipeRepository
){
    suspend operator fun invoke(recipeId: Int) {
        repository.toggleFavorite(recipeId)
    }
}