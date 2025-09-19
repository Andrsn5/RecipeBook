package com.example.recipebook.domain.usecase

import com.example.recipebook.domain.repository.RecipeRepository
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke() = repository.getFavoriteRecipes()
}