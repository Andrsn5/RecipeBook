package com.example.recipebook.domain.usecase.recipeUseCase

import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecipeDetailsUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: Int): Flow<Resource<Recipe>> = repository.getRecipeById(id)
}