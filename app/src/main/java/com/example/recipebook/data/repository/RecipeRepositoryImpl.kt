package com.example.recipebook.data.repository

import com.example.recipebook.data.local.RecipeDao
import com.example.recipebook.data.mapper.RecipeMapper
import com.example.recipebook.data.remote.RecipeApi
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: RecipeApi,
    private val dao: RecipeDao
): RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> =
        dao.getAll().map { RecipeMapper.entityListToDomain(it) }

    override fun searchRecipes(query: String): Flow<List<Recipe>> =
        dao.search(query).map { RecipeMapper.entityListToDomain(it) }

    override fun getRecipeById(id: String): Flow<Recipe> =
        dao.getById(id).map { it?.let { RecipeMapper.entityToDomain(it) } }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> =
        dao.getFavorites().map { RecipeMapper.entityListToDomain(it) }

    override suspend fun toggleFavorite(recipeId: String) {
        val recipe = dao.getById(recipeId)}
}