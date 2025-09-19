package com.example.recipebook.data.mapper

import com.example.recipebook.data.local.RecipeEntity
import com.example.recipebook.data.remote.RecipeDto
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
            lastUpdate = 0)

    fun entityToDomain(entity: RecipeEntity): RecipeDto =
        RecipeDto(
            id = entity.id,
            title = entity.title,
            imageUrl = entity.imageUrl,
            category = entity.category,
            description = entity.description,
            ingredients = json.decodeFromString(entity.ingredients),
            isFavorite = entity.isFavorite,
            lastUpdate = entity.lastUpdate)

    fun entityListToDomain(list: List<RecipeEntity>) = list.map { entityToDomain(it) }
    fun dtoListToEntity(list: List<RecipeDto>) = list.map { dtoToEntity(it) }
}