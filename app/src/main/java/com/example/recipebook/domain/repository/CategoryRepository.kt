package com.example.recipebook.domain.repository

import com.example.recipebook.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface  CategoryRepository {
    fun getCategories(): Flow<List<Category>>
}