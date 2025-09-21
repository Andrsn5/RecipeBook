package com.example.recipebook.data.mapper

import com.example.recipebook.data.local.categoryLocal.CategoryEntity
import com.example.recipebook.data.remote.categoryRemote.CategoryDto
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

    fun dtoToEntityCategory(dto: CategoryDto): CategoryEntity =
        CategoryEntity(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.imageUrl,
        )

    fun domainToEntityCategory(dto: Category): CategoryEntity =
        CategoryEntity(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.imageUrl,
        )


    fun entityListToDomainCategory(list: List<CategoryEntity>): List<Category> = list.map { entityToDomainCategory(it) }
    fun dtoListToEntityCategory(list: List<CategoryDto>): List<CategoryEntity> = list.map { dtoToEntityCategory(it) }

}