package com.example.recipebook.data.repository

import com.example.recipebook.data.local.categoryLocal.CategoryDao
import com.example.recipebook.data.mapper.CategoryMapper
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
): CategoryRepository {
    override fun getCategories(): Flow<Resource<List<Category>>> =
        channelFlow {
            try {
                val categories = listOf(
                    Category(1, "Main Course", "https://spoonacular.com/recipeImages/1-312x231.jpg"),
                    Category(2, "Dessert", "https://spoonacular.com/recipeImages/2-312x231.jpg"),
                    Category(3, "Appetizer", "https://spoonacular.com/recipeImages/3-312x231.jpg"),
                    Category(4, "Salad", "https://spoonacular.com/recipeImages/4-312x231.jpg"),
                    Category(5, "Breakfast", "https://spoonacular.com/recipeImages/5-312x231.jpg")
                )
                val entity = categories.map { CategoryMapper.domainToEntity(it) }
                dao.insertAll(entity)
                dao.getCategories()
                    .map {
                        Resource.Success(CategoryMapper.entityListToDomainCategory(it))
                    }
                    .collect { send(it) }

            } catch (e: Exception) {
                try {
                    dao.getCategories()
                        .map { list ->
                            if (list.isNotEmpty()) Resource.Success(CategoryMapper.entityListToDomainCategory(list))
                            else Resource.Error("No categories available")
                        }
                        .collect { send(it) }
                } catch (_: Exception) {
                    send(Resource.Error(e.localizedMessage ?: "Error loading categories"))
                }
            }
        }.flowOn(Dispatchers.IO)
}