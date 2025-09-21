package com.example.recipebook.domain.usecase

import com.example.recipebook.domain.repository.CategoryRepository
import com.example.recipebook.domain.repository.RecipeRepository
import javax.inject.Inject


class GetCategoriesUseCase @Inject constructor(
    private val recipeRepository: CategoryRepository
){
    operator fun invoke() = recipeRepository.getCategories()
}