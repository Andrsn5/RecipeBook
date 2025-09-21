package com.example.recipebook.data.repository


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
                // Получаем случайные рецепты вместо всех
                val response = api.getRandomRecipes(
                    number = 20,
                    apiKey = TODO()
                )
                response.recipes // Возвращаем список рецептов из response
            },
            saveFetchResult = { recipes ->
                // Сохраняем полученные рецепты
                dao.insertAll(recipes.map { RecipeMapper.dtoToEntity(it) })
            },
            shouldFetch = { cached -> cached.isEmpty() },
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

    override fun getRecipeById(id: String): Flow<Resource<Recipe>> =
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

    override suspend fun toggleFavorite(recipeId: String) {
        val recipe = dao.getById(recipeId).first()
        recipe?.let {
            dao.updateFavorite(recipeId, !it.isFavorite)
        }
    }
}