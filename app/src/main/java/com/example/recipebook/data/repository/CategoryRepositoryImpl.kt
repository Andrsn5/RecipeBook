package com.example.recipebook.data.repository

import com.example.recipebook.data.local.CategoryDao
import com.example.recipebook.data.mapper.CategoryMapper
import com.example.recipebook.data.remote.CategoryApi
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApi,
    private val dao: CategoryDao
): CategoryRepository {
    override fun getCategories(): Flow<List<Category>> =
        dao.getCategories().map { CategoryMapper.entityListToDomainCategory(it)}
}