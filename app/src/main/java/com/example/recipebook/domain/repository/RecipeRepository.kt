package com.example.recipebook.domain.repository

import com.example.recipebook.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun searchRecipes(query: String): Flow<List<Recipe>>
    fun getRecipeById(id: String): Flow<Recipe>
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    fun getCategories(): Flow<List<Recipe>>

    suspend fun toggleFavorite(recipeId: String)
}