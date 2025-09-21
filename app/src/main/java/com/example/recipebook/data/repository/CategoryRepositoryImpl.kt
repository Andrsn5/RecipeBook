package com.example.recipebook.data.repository

import com.example.recipebook.data.local.categoryLocal.CategoryDao
import com.example.recipebook.data.mapper.CategoryMapper
import com.example.recipebook.data.remote.categoryRemote.CategoryApi
import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.repository.CategoryRepository
import com.example.recipebook.presentation.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApi,
    private val dao: CategoryDao,
    private val networkMonitor: NetworkMonitor
): CategoryRepository {
    override fun getCategories(): Flow<Resource<List<Category>>> =
        flow {
            emit(Resource.Loading())
            try {
                // Spoonacular не имеет прямого endpoint для категорий
                // Используем ручную реализацию или получаем через поиск
                val categories = listOf(
                    Category("1", "Main Course", "main_course_url"),
                    Category("2", "Dessert", "dessert_url"),
                    Category("3", "Appetizer", "appetizer_url"),
                    Category("4", "Salad", "salad_url"),
                    Category("5", "Bread", "bread_url"),
                    Category("6", "Breakfast", "breakfast_url"),
                    Category("7", "Soup", "soup_url"),
                    Category("8", "Beverage", "beverage_url"),
                    Category("9", "Sauce", "sauce_url"),
                    Category("10", "Marinade", "marinade_url"),
                    Category("11", "Fingerfood", "fingerfood_url"),
                    Category("12", "Snack", "snack_url"),
                    Category("13", "Drink", "drink_url")
                )

                // Сохраняем в базу
                dao.insertAll(categories.map { CategoryMapper.domainToEntityCategory(it) })

                // Возвращаем из базы
                dao.getCategories()
                    .map { CategoryMapper.entityListToDomainCategory(it) }
                    .collect { result ->
                        emit(Resource.Success(result))
                    }

            } catch (e: Exception) {
                // Если ошибка сети, пытаемся получить из базы
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