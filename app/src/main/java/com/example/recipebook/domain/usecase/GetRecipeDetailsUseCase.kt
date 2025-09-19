package com.example.recipebook.domain.usecase

import com.example.recipebook.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeDetailsUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: String) = repository.getRecipeById(id)
}