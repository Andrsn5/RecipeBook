package com.example.recipebook.data.mapper

import com.example.recipebook.data.local.CategoryEntity
import com.example.recipebook.data.local.RecipeEntity
import com.example.recipebook.domain.model.Category
import kotlinx.serialization.json.Json

object CategoryMapper {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }


    fun entityToDomainCategory(entity: CategoryEntity): Category =
        Category(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.imageUrl,
        )


    fun entityListToDomainCategory(list: List<CategoryEntity>): List<Category> = list.map { entityToDomainCategory(it) }
}