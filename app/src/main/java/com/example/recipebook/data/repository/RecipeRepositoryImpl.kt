package com.example.recipebook.data.repository


import android.util.Log
import com.example.recipebook.data.local.recipeLocal.RecipeDao
import com.example.recipebook.data.mapper.RecipeMapper
import com.example.recipebook.data.remote.recipeRemote.RecipeApi
import com.example.recipebook.data.util.Resource
import com.example.recipebook.data.util.networkBoundResource
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.domain.repository.RecipeRepository
import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: RecipeApi,
    private val dao: RecipeDao,
    private val networkMonitor: NetworkMonitor
): RecipeRepository {

    override fun getAllRecipes(): Flow<Resource<List<Recipe>>> =
        networkBoundResource(
            query = { dao.getAll().map { RecipeMapper.entityListToDomain(it) } },
            fetch = {
                val response = api.getRandomRecipes(number = 20)
                response.recipes
            },
            saveFetchResult = { recipes ->
                val mergedEntities = recipes.map { dto ->
                    val entity = RecipeMapper.dtoToEntity(dto)
                    val existing = dao.getByIdOnce(entity.id)
                    if (existing != null) entity.copy(isFavorite = existing.isFavorite)
                    else entity
                }
                dao.insertAll(mergedEntities)
            },
            shouldFetch = { true },
            networkMonitor = networkMonitor
        )

    override fun searchRecipes(query: String): Flow<Resource<List<Recipe>>> =
        flow {
            emit(Resource.Loading())
            try {
                dao.search(query)
                    .map { RecipeMapper.entityListToDomain(it) }
                    .collect { result ->
                        emit(Resource.Success(result))
                    }
            } catch (e: Exception) {
                emit(Resource.Error<List<Recipe>>(e.localizedMessage ?: "Error searching"))
            }
        }

    override fun getRecipesByCategory(category: String): Flow<Resource<List<Recipe>>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = api.getRecipesByCategory(type = category, number = 20)
                val entities = response.results.map { RecipeMapper.dtoToEntity(it).copy(category = category) }
                val mergedEntities = entities.map { entity ->
                    val existing = dao.getByIdOnce(entity.id)
                    if (existing != null) entity.copy(isFavorite = existing.isFavorite)
                    else entity
                }
                dao.insertAll(mergedEntities)
                dao.getByCategory(category)
                    .map { RecipeMapper.entityListToDomain(it) }
                    .collect { result ->
                        emit(Resource.Success(result))
                    }
            } catch (e: Exception) {
                Log.e("RecipeRepositoryImpl", "Error loading recipes by category from network: ${e.message}")
                emit(Resource.Error<List<Recipe>>("No internet connection"))
            }
        }

    override fun getRecipeById(id: Int): Flow<Resource<Recipe>> =
        flow {
            emit(Resource.Loading())
            try {
                dao.getById(id)
                    .map { entity ->
                        entity?.let { RecipeMapper.entityToDomain(it) }
                            ?: throw NoSuchElementException("Recipe with id $id not found")
                    }
                    .collect { recipe ->
                        emit(Resource.Success(recipe))
                    }
            } catch (e: Exception) {
                emit(Resource.Error<Recipe>(e.localizedMessage ?: "Error loading recipe"))
            }
        }

    override fun getFavoriteRecipes(): Flow<Resource<List<Recipe>>> =
        flow {
            emit(Resource.Loading())
            try {
                dao.getFavorites()
                    .map { RecipeMapper.entityListToDomain(it) }
                    .collect { result ->
                        emit(Resource.Success(result))
                    }
            } catch (e: Exception) {
                emit(Resource.Error<List<Recipe>>(e.localizedMessage ?: "Error loading favorites"))
            }
        }

    override suspend fun toggleFavorite(recipeId: Int) {
        try {
            val recipe = dao.getById(recipeId).first()
            recipe?.let {
                val newFavoriteState = !it.isFavorite
                dao.updateFavorite(recipeId, newFavoriteState)
            } ?: run {
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
            val newEntities = response.recipes.map { dto ->
                val entity = RecipeMapper.dtoToEntity(dto)
                val existing = dao.getByIdOnce(entity.id)
                if (existing != null) entity.copy(isFavorite = existing.isFavorite)
                else entity
            }
            dao.insertAll(newEntities)
            Log.d("RecipeRepositoryImpl", "Inserted ${newEntities.size} recipes")
            // Оставляем последние 30 + все лайкнутые
            dao.deleteOldNonFavorites(keepCount = 30)
            val homeCount = dao.getHomeRecipesCount()
            Log.d("RecipeRepositoryImpl", "Home recipes count after cleanup: $homeCount")
            return response.recipes.size
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImpl", "Error loading more recipes: ${e.message}")
            throw e
        }
    }

    override suspend fun loadMoreRecipesByCategory(category: String, offset: Int): Int {
        try {
            Log.d("RecipeRepositoryImpl", "Loading more recipes for category: $category with offset: $offset")
            val response = api.getRecipesByCategory(type = category, number = 20, offset = offset)
            val newEntities = response.results.map { dto ->
                val entity = RecipeMapper.dtoToEntity(dto).copy(category = category)
                val existing = dao.getByIdOnce(entity.id)
                if (existing != null) entity.copy(isFavorite = existing.isFavorite)
                else entity
            }
            dao.insertAll(newEntities)
            Log.d("RecipeRepositoryImpl", "Inserted ${newEntities.size} recipes for category: $category")
            dao.deleteOldNonFavoritesForCategory(category = category, keepCount = 30)
            return response.results.size
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImpl", "Error loading more recipes by category: ${e.message}")
            throw e
        }
    }
}