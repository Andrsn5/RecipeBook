package com.example.recipebook.domain.repository

import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface RecipeRepository {
    fun getAllRecipes(): Flow<Resource<List<Recipe>>>
    fun searchRecipes(query: String): Flow<List<Recipe>>
    fun getRecipeById(id: String): Flow<Recipe>
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    suspend fun toggleFavorite(recipeId: String)
}