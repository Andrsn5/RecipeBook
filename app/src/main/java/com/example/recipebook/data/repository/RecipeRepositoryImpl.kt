package com.example.recipebook.data.repository

import com.example.recipebook.data.local.RecipeDao
import com.example.recipebook.data.mapper.RecipeMapper
import com.example.recipebook.data.remote.RecipeApi
import com.example.recipebook.data.util.Resource
import com.example.recipebook.data.util.networkBoundResource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.repository.RecipeRepository
import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: RecipeApi,
    private val dao: RecipeDao,
    private val networkMonitor: NetworkMonitor
): RecipeRepository {

    override fun getAllRecipes(): Flow<Resource<List<Recipe>>> =
        networkBoundResource(
            query = { dao.getAll().map { RecipeMapper.entityListToDomain(it) } },
            fetch = { api.getAll() },
            saveFetchResult = { response ->
                dao.insertAll(response.map { RecipeMapper.dtoToEntity(it) })
            },
            shouldFetch = { cached -> cached.isEmpty() },
            networkMonitor = networkMonitor
        )

    override fun searchRecipes(query: String): Flow<List<Recipe>> =
        dao.search(query).map { RecipeMapper.entityListToDomain(it) }

    override fun getRecipeById(id: String): Flow<Recipe> =
        dao.getById(id).map { entity ->
            entity?.let { RecipeMapper.entityToDomain(it) }
                ?: throw NoSuchElementException("Recipe with id $id not found")
        }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> =
        dao.getFavorites().map { RecipeMapper.entityListToDomain(it) }

    override suspend fun toggleFavorite(recipeId: String) {
        val recipe = dao.getById(recipeId).first()
        recipe?.let {
            dao.updateFavorite(recipeId, !it.isFavorite)
        }
    }
}