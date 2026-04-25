package com.example.recipebook.data.repository


import android.util.Log
import com.example.recipebook.data.local.recipeLocal.RecipeDao
import com.example.recipebook.data.local.recipeLocal.RecipeEntity
import com.example.recipebook.data.mapper.RecipeMapper
import com.example.recipebook.data.remote.recipeRemote.RecipeApi
import com.example.recipebook.data.util.Resource
import com.example.recipebook.data.util.networkBoundResource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.repository.RecipeRepository
import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: RecipeApi,
    private val dao: RecipeDao,
    private val networkMonitor: NetworkMonitor
) : RecipeRepository {

    override fun getAllRecipes(): Flow<Resource<List<Recipe>>> {
        val domain = dao.getAll().map { RecipeMapper.entityListToDomain(it) }
        return networkBoundResource(
            query = { domain  },
            fetch = { api.getRandomRecipes(number = 20).recipes },
            saveFetchResult = { recipes ->
                val entity = recipes.map { RecipeMapper.dtoToEntity(it) }
                dao.upsertPreservingFavorites(entity)
            },
            shouldFetch = { true },
            networkMonitor = networkMonitor
        )
    }

    override fun searchRecipes(query: String): Flow<Resource<List<Recipe>>> =
        dao.search(query)
            .map<List<RecipeEntity>, Resource<List<Recipe>>> {
                Resource.Success(RecipeMapper.entityListToDomain(it))
            }
            .catch { e -> emit(Resource.Error(e.localizedMessage ?: "Error searching")) }
            .flowOn(Dispatchers.IO)

    override fun getRecipesByCategory(category: String): Flow<Resource<List<Recipe>>> =
        flow {
            try {
                val response = api.getRecipesByCategory(type = category, number = 20)
                val entities = response.results.map {
                    RecipeMapper.dtoToEntity(it).copy(category = category)
                }
                dao.upsertPreservingFavorites(entities)
            } catch (e: Exception) {
                Log.e("RecipeRepositoryImpl", "Error loading by category from network: ${e.message}")
                emit(Resource.Error("No internet connection"))
            }

            emitAll(
                dao.getByCategory(category)
                    .map { Resource.Success(RecipeMapper.entityListToDomain(it)) }
            )
        }.flowOn(Dispatchers.IO)

    override fun getRecipeById(id: Int): Flow<Resource<Recipe>> =
        dao.getById(id)
            .map { entity ->
                val recipe = entity?.let { RecipeMapper.entityToDomain(it) }
                    ?: throw NoSuchElementException("Recipe with id $id not found")
                Resource.Success(recipe) as Resource<Recipe>
            }
            .catch { e -> emit(Resource.Error(e.localizedMessage ?: "Error loading recipe")) }
            .flowOn(Dispatchers.IO)

    override fun getFavoriteRecipes(): Flow<Resource<List<Recipe>>> =
        dao.getFavorites()
            .map<List<RecipeEntity>, Resource<List<Recipe>>> {
                Resource.Success(RecipeMapper.entityListToDomain(it))
            }
            .catch { e -> emit(Resource.Error(e.localizedMessage ?: "Error loading favorites")) }
            .flowOn(Dispatchers.IO)

    override suspend fun toggleFavorite(recipeId: Int) {
        try {
            val recipe = dao.getByIdOnce(recipeId)
            if (recipe != null) {
                dao.updateFavorite(recipeId, !recipe.isFavorite)
            } else {
                Log.e("RecipeRepositoryImpl", "Recipe with id $recipeId not found in DB")
            }
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImpl", "Error toggling favorite: ${e.message}")
        }
    }

    override suspend fun loadMoreRecipes(offset: Int): Int {
        try {
            Log.d("RecipeRepositoryImpl", "Loading more recipes with offset: $offset")
            val response = api.getRandomRecipes(number = 20, offset = offset)
            dao.upsertPreservingFavorites(response.recipes.map { RecipeMapper.dtoToEntity(it) })
            dao.deleteOldNonFavorites(keepCount = 30)
            Log.d("RecipeRepositoryImpl", "Home count: ${dao.getHomeRecipesCount()}")
            return response.recipes.size
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImpl", "Error loading more recipes: ${e.message}")
            throw e
        }
    }

    override suspend fun loadMoreRecipesByCategory(category: String, offset: Int): Int {
        try {
            Log.d("RecipeRepositoryImpl", "Loading more for category: $category, offset: $offset")
            val response = api.getRecipesByCategory(type = category, number = 20, offset = offset)
            dao.upsertPreservingFavorites(
                response.results.map { RecipeMapper.dtoToEntity(it).copy(category = category) }
            )
            dao.deleteOldNonFavoritesForCategory(category = category, keepCount = 30)
            Log.d("RecipeRepositoryImpl", "Inserted ${response.results.size} for category: $category")
            return response.results.size
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImpl", "Error loading more by category: ${e.message}")
            throw e
        }
    }
}