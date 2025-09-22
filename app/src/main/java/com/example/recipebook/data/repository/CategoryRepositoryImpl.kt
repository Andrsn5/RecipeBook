package com.example.recipebook.data.repository

import com.example.recipebook.data.local.categoryLocal.CategoryDao
import com.example.recipebook.data.mapper.CategoryMapper
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
): CategoryRepository {
    override fun getCategories(): Flow<Resource<List<Category>>> =
        flow {
            emit(Resource.Loading())
            try {
                val categories = listOf(
                    Category(1, "Main Course", "https://spoonacular.com/recipeImages/1-312x231.jpg"),
                    Category(2, "Dessert", "https://spoonacular.com/recipeImages/2-312x231.jpg"),
                    Category(3, "Appetizer", "https://spoonacular.com/recipeImages/3-312x231.jpg"),
                    Category(4, "Salad", "https://spoonacular.com/recipeImages/4-312x231.jpg"),
                    Category(5, "Breakfast", "https://spoonacular.com/recipeImages/5-312x231.jpg")
                )


                dao.insertAll(categories.map { CategoryMapper.domainToEntity(it) })


                dao.getCategories()
                    .map { CategoryMapper.entityListToDomainCategory(it) }
                    .collect { result ->
                        emit(Resource.Success(result))
                    }

            } catch (e: Exception) {
                try {
                    dao.getCategories()
                        .map { CategoryMapper.entityListToDomainCategory(it) }
                        .collect { result ->
                            if (result.isNotEmpty()) {
                                emit(Resource.Success(result))
                            } else {
                                emit(Resource.Error("No categories available"))
                            }
                        }
                } catch (dbError: Exception) {
                    emit(Resource.Error(e.localizedMessage ?: "Error loading categories"))
                }
            }
        }
}