package com.example.recipebook.data.mapper

import com.example.recipebook.data.local.RecipeEntity
import com.example.recipebook.data.remote.RecipeDto
import com.example.recipebook.domain.model.Recipe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RecipeMapper {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    fun dtoToEntity(dto: RecipeDto): RecipeEntity =
        RecipeEntity(
            id = dto.id,
            title = dto.title,
            imageUrl = dto.imageUrl,
            category = dto.category,
            description = dto.description,
            ingredients = json.encodeToString(dto.ingredients ?: emptyList()),
            isFavorite = false,
            lastUpdate = System.currentTimeMillis()
        )

    fun entityToDomain(entity: RecipeEntity): Recipe =
        Recipe(
            id = entity.id,
            name = entity.title,
            description = entity.description,
            imageUrl = entity.imageUrl,
            category = entity.category,
            ingredients = try {
                json.decodeFromString(entity.ingredients)
            } catch (e: Exception) {
                emptyList()
            },
            favourite = entity.isFavorite
        )

    fun entityListToDomain(list: List<RecipeEntity>): List<Recipe> = list.map { entityToDomain(it) }
    fun dtoListToEntity(list: List<RecipeDto>): List<RecipeEntity> = list.map { dtoToEntity(it) }
}