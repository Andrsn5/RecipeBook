package com.example.recipebook.domain.usecase.recipeUseCase

import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
){
    operator fun invoke(query: String):Flow<Resource<List<Recipe>>> = recipeRepository.searchRecipes(query)
}