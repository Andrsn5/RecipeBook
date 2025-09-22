package com.example.recipebook.data.mapper

import com.example.recipebook.data.local.categoryLocal.CategoryEntity
import com.example.recipebook.domain.model.Category


object CategoryMapper {

    fun entityToDomain(entity: CategoryEntity): Category =
        Category(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.imageUrl,
        )

    fun domainToEntity(category: Category): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            imageUrl = category.imageUrl,
        )
    }

    fun entityListToDomainCategory(category: List<CategoryEntity>): List<Category> = category.map{ entityToDomain(it)}
}