package com.example.recipebook.domain.usecase.categoryUseCase

import com.example.recipebook.data.util.Resource
import com.example.recipebook.domain.model.Category
import com.example.recipebook.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val recipeRepository: CategoryRepository
){
    operator fun invoke(): Flow<Resource<List<Category>>> = recipeRepository.getCategories()
}